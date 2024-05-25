package com.exam.controller;


import com.exam.common.Result;
import com.exam.service.IRoleService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 角色 前端控制器
 * </p>
 *
 * @author 洛克
 * @since 2024-04-21
 */
@RestController
@RequestMapping("/api/role")
public class RoleController {
    @Resource
    private IRoleService roleService;
    @PreAuthorize("hasAnyRole('TEACHER','STUDENT','ADMIN')")
    @GetMapping("/getRoles")
    public Result<?> getRoles() {
        return roleService.getRoles();
    }

}
