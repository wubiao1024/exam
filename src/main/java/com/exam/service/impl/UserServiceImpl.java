package com.exam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.common.Result;
import com.exam.entity.LoginUser;
import com.exam.entity.User;
import com.exam.mapper.UserMapper;
import com.exam.service.IUserService;
import com.exam.utils.JwtUtils;
import com.exam.utils.RedisCache;
import jakarta.annotation.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
    private UserMapper userMapper;

    // 在配置中注入的
    @Resource
    private AuthenticationManager authenticationManager;

    // RedisCache
    @Resource
    private RedisCache redisCache;

    @Override
    public User findUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public Result<HashMap<String, String>> login(User user) {
        Authentication authenticate = null;
        try {
            // AuthenticationManager authenticate证行用户以证
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        } catch (AuthenticationException e) {
            // 认证失败
//            System.out.println("用户登录认证失败");
            return Result.fail("用户名或密码错误");
        }

        // 如果认证通过  把认证信息存入SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        //使用用户id 生成 token 存入redis 并返回
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        Long id = loginUser.getUser().getId();
        String token = JwtUtils.createToken(id);

        // 存入Redis
        redisCache.setCacheObject("login:" + id.toString(), loginUser);

        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        return Result.success("登录成功", map);
    }

    @Override
    public Result<?> loginOut() {
        //获取SecurityContextHolder 的用户id
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // 删除redis
        redisCache.deleteObject("login:" + loginUser.getUser().getId().toString());
        // 删除SecurityContextHolder 的信息
        SecurityContextHolder.clearContext();
        //返回响应消息
        return Result.success("注销成功");

    }

    @Override
    public Result<?> getCurrentUserByToken(String token) {
        // 解析token获取id
        Long id = JwtUtils.getIdByToken(token);
        if (id == -1) {
            return Result.fail("登录信息已失效，请重新登录");
        }
        // 获取用户信息
        User user = userMapper.selectById(id);
        HashMap<String, Object> res = new HashMap<>();
        res.put("nickname", user.getNickname());
//        res.put("role", user.getRole());
        res.put("avatar", user.getAvatar());
        res.put("id", user.getId());
        res.put("username", user.getUsername());
        res.put("contactInfo", user.getContactInfo());
        res.put("realName", user.getRealName());
        //返回
        return Result.success(res);
    }


}

