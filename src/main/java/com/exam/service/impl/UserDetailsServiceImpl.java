package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.exam.entity.LoginUser;
import com.exam.entity.Role;
import com.exam.entity.User;
import com.exam.entity.UserRole;
import com.exam.mapper.RoleMapper;
import com.exam.mapper.UserMapper;
import com.exam.mapper.UserRoleMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserMapper userMapper;
    @Resource
    UserRoleMapper userRoleMapper;
    @Resource
    RoleMapper roleMapper;
    // TODO 自定义用户名

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户信息
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(queryWrapper);

        // 如果查询结果为空就抛出错误
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }

        //TODO 查询接口权限信息
        List<String> interfaceList = userMapper.getInterfaceByUserId(user.getId());

        //TODO 查询角色具有的权限信息
        LambdaQueryWrapper<UserRole> userRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoleLambdaQueryWrapper
                .select(UserRole::getRoleId)
                .eq(UserRole::getUserId, user.getId());

        List<UserRole> userRoles = userRoleMapper.selectList(userRoleLambdaQueryWrapper);
        // TODO ROLE+RoleName格式 对应用户具有的角色
        List<String> list =
                userRoles.stream().map(userRole ->"ROLE_" + roleMapper.selectOne(new LambdaQueryWrapper<Role>().select(Role::getRoleName).eq(Role::getId,
                userRole.getRoleId())).getRoleName()).toList();

        //权限信息合并
        interfaceList.addAll(list);

        //将用户信息封装为 UserDetails 返回
        return new LoginUser(user, interfaceList);
    }
}
