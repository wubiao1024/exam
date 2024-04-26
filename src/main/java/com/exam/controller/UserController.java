package com.exam.controller;

import com.exam.POJO.DTO.LoginUserDTO;
import com.exam.common.Result;
import com.exam.entity.User;
import com.exam.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Resource
    private IUserService userService;


    @PostMapping("/api/user/login")
    public Result<?> login(@RequestBody LoginUserDTO loginUserDTO) {
        User user = new User();
        BeanUtils.copyProperties(loginUserDTO, user);
        // 登录
        return userService.login(user);
    }


    @GetMapping("/api/user/logout")
    public Result<?> loginOut() {
        return userService.loginOut();
    }

    @GetMapping("/api/findUserById")
    public User findUserById(Long id) {
        return userService.findUserById(id);
    }

    @GetMapping("/api/user/currentUser")
    public Result<?> currentUser(@RequestHeader String token) {
        return userService.getCurrentUserByToken(token);
    }
}

