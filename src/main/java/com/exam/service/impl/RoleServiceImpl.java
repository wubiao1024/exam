package com.exam.service.impl;

import com.exam.POJO.BO.RoleBO;
import com.exam.common.Result;
import com.exam.entity.Role;
import com.exam.mapper.RoleMapper;
import com.exam.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 角色 服务实现类
 * </p>
 *
 * @author 洛克
 * @since 2024-04-21
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {
    @Resource
    private RoleMapper roleMapper;

    @Override
    public Result<?> getRoles() {
        List<Role> roles = roleMapper.selectList(null);
        ArrayList<RoleBO> roleBOS = new ArrayList<>();
        for (Role role : roles) {
            RoleBO roleBO = new RoleBO();
            BeanUtils.copyProperties(role, roleBO);
            roleBOS.add(roleBO);
        }
        return Result.success(roleBOS);


    }
}
