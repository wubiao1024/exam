package com.exam.service;

import com.exam.POJO.DTO.UpdatePasswordDTO;
import com.exam.POJO.DTO.UpdateUserInfoDTO;
import com.exam.POJO.DTO.UserDTO;
import com.exam.POJO.DTO.UserSelectDTO;
import com.exam.common.Result;
import com.exam.entity.User;

import java.util.List;

public interface IUserService {
    public User findUserById(Long id);

    Result<?> login(User user);

    Result<?> loginOut(String token);

    Result<?> getCurrentUserByToken(String token);

    // TODO 权限管理,动态路由
    List<String> getInterfaceAuthListById(Long userId);

    Result<?> addUser(UserDTO userDTO);

    Result<?> getUsers(UserSelectDTO userSelectDTO);

    Result<?> updateUser(UserDTO userDTO);

    Result<?> deleteUser(Long id);

    Result<?> updateInfo(UpdateUserInfoDTO user);

    Result<?> getUsersWithRole(UserSelectDTO userSelectDTO);

    Result<?> updatePassword(UpdatePasswordDTO passwordDTO);
}
