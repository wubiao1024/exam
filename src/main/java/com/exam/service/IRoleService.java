package com.exam.service;

import com.exam.common.Result;
import com.exam.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 角色 服务类
 * </p>
 *
 * @author 洛克
 * @since 2024-04-21
 */
public interface IRoleService extends IService<Role> {
    Result<?> getRoles();
}
