package com.exam.controller;


import com.exam.common.Result;
import com.exam.service.impl.SubjectServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 洛克
 * @since 2024-04-21
 */
@RestController
@RequestMapping("/api/subject")
public class SubjectController {
    @Resource
    SubjectServiceImpl subjectService;

    @GetMapping("/getAllSubject")
    public Result<?> getAllSubject() {
        return subjectService.getAllSubject();
    }

}



