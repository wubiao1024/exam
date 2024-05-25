package com.exam.mapper;

import com.exam.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 拾光者
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2024-03-05 05:34:27
* @Entity com.exam.entity.User
*/
public interface UserMapper extends BaseMapper<User> {
    // 获取用户的动态路由信息
    public List<String> getPathByUserId(Long userId);
    // 获取用户的接口权限
    public List<String> getInterfaceByUserId(Long userId);
}




