package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.Subject;
import com.exam.mapper.SubjectMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class SubjectServiceImplTest {
    @Resource
    SubjectMapper subjectMapper;

    @Test
    public void getAllSubject() {
        LambdaQueryWrapper<Subject> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .select(Subject::getId, Subject::getName, Subject::getEnglishName);
        System.out.println(subjectMapper.selectMaps(wrapper));
        subjectMapper.selectPage(new Page<>(1L, 2L), wrapper);
    }

    @Test
    public void test() {
        System.out.println(new Date());
    }


}