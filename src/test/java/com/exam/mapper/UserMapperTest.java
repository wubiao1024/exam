package com.exam.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        System.out.println(encode);
        System.out.println(encode1);

    }

    @Test
    public void testUserMapper() {
        User user = userMapper.selectById("1");
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean b = bCryptPasswordEncoder.matches("admin123", user.getPassword());
        System.out.println(b);
    }

    @Test
    public void testUserMapper1() {
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>().select(User::getId,User::getPassword));
        users.forEach(System.out::println);

        System.out.println();
        System.out.println("===");

        List<User> users1 =
                userMapper.selectList(new LambdaQueryWrapper<User>().select(User::getId).select(User::getRealName).gt(User::getId, 1));
        users1.forEach(System.out::println);
    }
}