package com.exam.service;

import com.exam.common.Result;
import com.exam.entity.User;

public interface IUserService {
    public User findUserById(Long id);

    Result<?> login(User user);

    Result<?> loginOut();

    Result<?> getCurrentUserByToken(String token);
}
