package com.exam.controller;

import com.exam.POJO.BO.UserBO;
import com.exam.POJO.DTO.*;
import com.exam.common.Result;
import com.exam.entity.User;
import com.exam.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Resource
    private IUserService userService;

    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginUserDTO loginUserDTO) {
        User user = new User();
        BeanUtils.copyProperties(loginUserDTO, user);
        if(loginUserDTO.getRole() == null){
            return Result.fail("请选择身份");
        }

        // user设置当前的登录的身份
        user.setCurrentRole(loginUserDTO.getRole());
        // 登录
        return userService.login(user);
    }


    @GetMapping("/logout")
    public Result<?> loginOut(@RequestHeader String token) {
        return userService.loginOut(token);
    }

    // 查询当前用户的信息
    @GetMapping("/currentUser")
    public Result<?> currentUser(@RequestHeader String token) {
        return userService.getCurrentUserByToken(token);
    }

    @PostMapping("/addUser")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> addUser(@RequestBody UserDTO userDTO) {
       return userService.addUser(userDTO);
    }

    @PostMapping("/getUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> getUsers(@RequestBody UserSelectDTO userSelectDTO) {
        return userService.getUsers(userSelectDTO);
    }

    // 修改用户权限信息
    @PutMapping("/updateUser")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<?> editUser(@RequestBody UserDTO userDTO) {
        return userService.updateUser(userDTO);
    }

    // 更新个人信息
    @PreAuthorize("hasAnyRole('ADMIN','USER','TEACHER')")
    @PutMapping("/updateUserInfo")
    public Result<?> updateInfo(@RequestBody UpdateUserInfoDTO userDTO) {
        return userService.updateInfo(userDTO);
    }

    // 修改密码
    @PreAuthorize("hasAnyRole('ADMIN','USER','TEACHER')")
    @PutMapping("/updatePassword")
    public Result<?> updatePassword(@RequestBody UpdatePasswordDTO passwordDTO) {
        return userService.updatePassword(passwordDTO);
    }

    // 删除用户信息
    // TODO : DELETE 判断对其他表的影响
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteUser/{id}")
    public Result<?> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }



    //获取所有的学生用户
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @PostMapping("/getUsersWithRole")
    public Result<?> getUsersWithRole(@RequestBody UserSelectDTO userSelectDTO) {
        return userService.getUsersWithRole(userSelectDTO);
    }

}

