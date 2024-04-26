package com.exam.mapper;

import com.exam.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@SpringBootTest
class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void test() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode("admin123");
        String encode1 = bCryptPasswordEncoder.encode("admin123");
        // 同一个密码，加密多次，输出不同
        // $2a$10$Bpv.N0DtQzQhMZMVPcwNle8ar7/RboqnUoKWrXte7a.IjSoOp9lhq
        boolean b = bCryptPasswordEncoder.matches("admin123", encode1);
        System.out.println(b);
        System.out.println(encode1);

    }

    @Test
    public void testUserMapper() {
        User user = userMapper.selectById("1");
        List<User> users = userMapper.selectList(null);
        System.out.println(user);
        System.out.println(users);
    }
}