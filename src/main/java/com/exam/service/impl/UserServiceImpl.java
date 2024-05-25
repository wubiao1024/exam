package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.POJO.BO.RoleBO;
import com.exam.POJO.DTO.*;
import com.exam.common.Result;
import com.exam.entity.Class;
import com.exam.entity.LoginUser;
import com.exam.entity.Role;
import com.exam.entity.User;
import com.exam.entity.UserRole;
import com.exam.mapper.ClassMapper;
import com.exam.mapper.RoleMapper;
import com.exam.mapper.UserMapper;
import com.exam.mapper.UserRoleMapper;
import com.exam.service.IUserService;
import com.exam.utils.JwtUtils;
import com.exam.utils.PageUtils;
import com.exam.utils.RedisCache;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    private static final String DEFAULT_PASSWORD = "admin123";
    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private ClassMapper classMapper;

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
            System.out.println(e.getLocalizedMessage());
//            System.out.println("用户登录认证失败");
            return Result.fail("用户名或密码错误");
        }

        // 如果认证通过  把认证信息存入SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authenticate);


        // 获取人造毛的用户信息
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();

        //使用用户id 生成 token 存入redis 并返回
        Long id = loginUser.getUser().getId();
        String token = JwtUtils.createToken(id);

        // 存入Redis
        redisCache.setCacheObject("login:" + id.toString(), loginUser);

        // 返回
        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        return Result.success("登录成功", map);
    }

    @Override
    public Result<?> loginOut(String token) {
       //  Long userId = JwtUtils.getIdByToken(token);

        this.logOut();
        //返回响应消息
        return Result.success("退出成功");

    }

    @Override
    public Result<?> getCurrentUserByToken(String token) {
        // 解析token获取id
        Long id = JwtUtils.getIdByToken(token);
        if (id == -1) {
            return Result.fail("登录信息已失效，请重新登录");
        }
        // 获取用户信息
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getId, id));
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO, "password");

        // 班级
        String className = classMapper.selectOne(new LambdaQueryWrapper<Class>().select(Class::getClassName).eq(Class::getId,
                userDTO.getClassId())).getClassName();
        userDTO.setClassName(className);

        // 查询用户具有的权限名称
        List<RoleBO> roleNameList = getRoleBOsByUserId(id);
        userDTO.setRoleNames(roleNameList);
        List<Long> roleIds = roleNameList.stream().map(RoleBO::getId).toList();
        // 查询用户具有的角色id9
        userDTO.setRoleIds(roleIds);
        return Result.success(userDTO);
    }



    @Override
    public List<String> getInterfaceAuthListById(Long userId) {
        return userMapper.getInterfaceByUserId(userId);
    }

    @Override
    public Result<?> addUser(UserDTO userDTO) {
        if (userDTO ==null){
            return Result.fail("用户信息不能为空");
        }
        //判断username是否存在
        User user1 = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername,userDTO.getUsername()));
        if (user1 != null){
            return Result.fail("用户名已存在");
        }
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setRealName(userDTO.getRealName());
        user.setId(redisCache.getUID());
        // 生成初始密码
        String password = new BCryptPasswordEncoder().encode(DEFAULT_PASSWORD);
        user.setPassword(password);
        // 插入
        userMapper.insert(user);
        List<Long> roleIds = userDTO.getRoleIds();
        // 给用户分配角色
        if(roleIds == null || roleIds.isEmpty()){
            return Result.success("用户添加成功");
        }
        AtomicReference<Boolean> flag = new AtomicReference<>(true);
        roleIds.forEach(roleId -> {
            try {
                UserRole userRole = new UserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(roleId);
                userRole.setId(redisCache.getUID());
                Role role = roleMapper.selectOne(new LambdaQueryWrapper<Role>().select(Role::getRoleName).eq(Role::getId, roleId));
                userRole.setDescription(user.getUsername()+":"+role.getRoleName());
                userRoleMapper.insert(userRole);
            } catch (Exception e) {
                System.out.println("用户角色分配失败");
                flag.set(false);
            }
        });
        if (flag.get()){
            return Result.success("用户添加成功");
        }else{
            return Result.fail("用户添加成功,用户角色分配失败,检查角色id是否正确！");
        }
    }

    @Override
    public Result<?> getUsers(UserSelectDTO userSelectDTO) {
        Page<User> selectPage = new Page<>(userSelectDTO.getCurrent(), userSelectDTO.getPageSize());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //身份
        String roleId = userSelectDTO.getRoleId();
        List<Long> userIds = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, roleId)).stream().map(UserRole::getUserId).toList();
        if(!userIds.isEmpty()){
            queryWrapper.in(User::getId, userIds);
        }
        // 用户名
        if (userSelectDTO.getUsername() != null && !userSelectDTO.getUsername().isEmpty()){
            queryWrapper.like(User::getUsername,userSelectDTO.getUsername());
        }

        //真实姓名
        if(userSelectDTO.getRealName() != null && !userSelectDTO.getRealName().isEmpty()){
            queryWrapper.like(User::getRealName,userSelectDTO.getRealName());
        }

        // 班级id
        if(userSelectDTO.getClassId() != null ){
            queryWrapper.eq(User::getClassId,userSelectDTO.getClassId());
        }
        // 用户id
        if(userSelectDTO.getId() != null ){
            queryWrapper.eq(User::getId,userSelectDTO.getId());
        }

        Page<User> userPage = userMapper.selectPage(selectPage, queryWrapper);

        List<UserResDTO> userResDTOList = userPage.getRecords().stream().map(user -> {
            UserResDTO userResDTO = new UserResDTO();
            BeanUtils.copyProperties(user, userResDTO);
            //班级名称
            Class aClass = classMapper.selectOne(new LambdaQueryWrapper<Class>().select(Class::getClassName).eq(Class::getId,
                    user.getClassId()));
            if(aClass != null){
                userResDTO.setClassName(aClass.getClassName());
            }
            // 获取用户具有的角色信息
            List<RoleBO> list = getRoleBOsByUserId(user.getId());
            // 设置角色信息
            userResDTO.setRoles(list);
            return userResDTO;
        }).toList();



        return Result.success(PageUtils.getResMapByPage(userPage, "userList",userResDTOList));
    }

    @Override
    public Result<?> getUsersWithRole(UserSelectDTO userSelectDTO) { // STUDENT

        Page<User> selectPage = new Page<>(userSelectDTO.getCurrent(), userSelectDTO.getPageSize());

        String roleName = userSelectDTO.getRole();
        Role role1 = roleMapper.selectOne(new LambdaQueryWrapper<Role>().select(Role::getId).eq(Role::getRoleName, roleName));



        List<Long> userIdList =null;

        if(role1 != null){
            userIdList =
                    userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, role1.getId())).stream().map(UserRole::getUserId).toList();
        }


        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        // 用户身份
        if(userIdList != null){
            queryWrapper.in(User::getId,userIdList);
        }
        // 用户名
        if (userSelectDTO.getUsername() != null && !userSelectDTO.getUsername().isEmpty()){
            queryWrapper.like(User::getUsername,userSelectDTO.getUsername());
        }

        //真实姓名
        if(userSelectDTO.getRealName() != null && !userSelectDTO.getRealName().isEmpty()){
            queryWrapper.like(User::getRealName,userSelectDTO.getRealName());
        }

        // 班级id
        if(userSelectDTO.getClassId() != null ){
            queryWrapper.eq(User::getClassId,userSelectDTO.getClassId());
        }
        // 用户id
        if(userSelectDTO.getId() != null ){
            queryWrapper.eq(User::getId,userSelectDTO.getId());
        }

        Page<User> userPage = userMapper.selectPage(selectPage, queryWrapper);


        List<UserResDTO> userResDTOList = userPage.getRecords().stream().map(user -> {
            UserResDTO userResDTO = new UserResDTO();
            BeanUtils.copyProperties(user, userResDTO);
            //班级名称
            Class aClass = classMapper.selectOne(new LambdaQueryWrapper<Class>().select(Class::getClassName).eq(Class::getId,
                    user.getClassId()));
            if(aClass != null){
                userResDTO.setClassName(aClass.getClassName());
            }
            // 获取用户具有的角色信息
            List<RoleBO> list = getRoleBOsByUserId(user.getId());
            // 设置角色信息
            userResDTO.setRoles(list);
            return userResDTO;
        }).map(user -> {
            UserResDTO userResDTO = new UserResDTO();
            BeanUtils.copyProperties(user, userResDTO,"roles");
            return userResDTO;
            //班级名称
        }).toList();

        return Result.success(PageUtils.getResMapByPage(userPage, "userList",userResDTOList));
    }



    @Override
    public Result<?> updateUser(UserDTO userDTO) {
        if (userDTO == null) {
            return Result.fail("用户信息不能为空");
        }

        // 管理观只能更新用户的班级信息
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getId,userDTO.getId()));
        user.setClassId(userDTO.getClassId());
        userMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getId, userDTO.getId()));

        // 更新权限
        //删除原来的角色
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userDTO.getId()));
        //给用户分配信息角色
        List<Long> roleIds = userDTO.getRoleIds();
        if (roleIds == null || roleIds.isEmpty()) {
            return Result.success("用户更新成功");
        }
        AtomicReference<Boolean> flag = new AtomicReference<>(true);
        roleIds.forEach(roleId -> {
            try {
                UserRole userRole = new UserRole();
                userRole.setUserId(userDTO.getId());
                userRole.setRoleId(roleId);
                userRole.setId(redisCache.getUID());
                Role role = roleMapper.selectOne(new LambdaQueryWrapper<Role>().select(Role::getRoleName).eq(Role::getId, roleId));
                userRole.setDescription(userDTO.getUsername() + ":" + role.getRoleName());
                userRoleMapper.insert(userRole);
            } catch (Exception e) {
                System.out.println("用户角色分配失败");
                flag.set(false);
            }
        });
        if (flag.get()) {
            return Result.success("用户更新成功");
        } else {
            return Result.fail("用户更新成功,用户角色分配失败,检查角色id是否正确！");
        }
    }

    @Override
    public Result<?> deleteUser(Long id) {
        if (id == null) {
            return Result.fail("用户id不能为空");
        }
        // 删除用户具有的角色
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, id));
        // 删除用户
        userMapper.deleteById(id);
        return Result.success("删除成功");
    }

    // 用户更新个人基本信息
    @Override
    public Result<?> updateInfo(UpdateUserInfoDTO userInfoDTO) {
        // 判断用户信息是否为空
        if (userInfoDTO == null) {
            return Result.fail("用户信息不能为空");
        }
        //用户名不能重复存在
        User user1 = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername,userInfoDTO.getUsername()));
        if (user1 != null){
            return Result.fail("用户名已存在,请使用其他用户名");
        }
        boolean exists = userMapper.exists(new LambdaQueryWrapper<User>().eq(User::getId, userInfoDTO.getId()));
        if (!exists){
            return Result.fail("用户不存在");
        }
        User user = new User();
        BeanUtils.copyProperties(userInfoDTO, user);
        // 更新用户信息
        userMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getId, user.getId()));
        return Result.success("更新成功");
    }

    @Override
    public Result<?> updatePassword(UpdatePasswordDTO passwordDTO) {
        if(passwordDTO == null || passwordDTO.getOldPassword() == null || passwordDTO.getNewPassword() == null ||passwordDTO.getId() == null){
            return Result.fail("参数不正确！");
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getId, passwordDTO.getId()));
        if(user == null){
            return Result.fail("用户不存在！");
        }
        // 判断原来的密码是否正确
        if (!new BCryptPasswordEncoder().matches(passwordDTO.getOldPassword(), user.getPassword())) {
            return Result.fail("原密码错误，修改失败");
        }
        // 密码加密
        String newPassword = new BCryptPasswordEncoder().encode(passwordDTO.getNewPassword());
        user.setPassword(newPassword);
        userMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getId, user.getId()));
        this.logOut();
        return Result.success("密码修改成功,请重新登录");
    }

    // 退出登录
    public void logOut(){
        // 退出登录
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // 删除redis
        redisCache.deleteObject("login:" + loginUser.getUser().getId());
        // 删除SecurityContextHolder 的信息
        SecurityContextHolder.clearContext();
    }



    //HELPER 根据用户id获取用户具有的身份信息
    public List<RoleBO> getRoleBOsByUserId(Long userId) {
        LambdaQueryWrapper<UserRole> userRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoleLambdaQueryWrapper
                .select(UserRole::getRoleId)
                .eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleLambdaQueryWrapper);
        return
                userRoles.stream().map(userRole -> {
                    Role role = roleMapper.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getId,
                            userRole.getRoleId()));
                    RoleBO roleBO = new RoleBO();
                    BeanUtils.copyProperties(role, roleBO);
                    return roleBO;
                }).toList();
    }

}

