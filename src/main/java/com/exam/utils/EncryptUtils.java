package com.exam.utils;

import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EncryptUtils {
    @Resource
    private PasswordEncoder passwordEncoder;

}
