package com.exam.controller;


import com.exam.POJO.DTO.QuestionDTO;
import com.exam.POJO.DTO.QuestionSelectDTO;
import com.exam.POJO.DTO.QuestionUpdateDTO;
import com.exam.common.Result;
import com.exam.entity.Question;
import com.exam.service.IQuestionService;
import com.exam.service.impl.ExamRecordServiceImpl;
import com.exam.service.impl.QuestionServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 问题 前端控制器
 * </p>
 *
 * @author 洛克
 * @since 2024-04-21
 */
@RestController
@RequestMapping("/api/question")
public class QuestionController {
    @Resource
    IQuestionService questionService;

    @PutMapping("/add")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<?> addQuestion(@RequestBody QuestionDTO question, @RequestHeader("token") String token) {
        return questionService.addQuestion(question, token);
    }

    // TODO DELETE 删除对其他表的影响需要限制一下
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<?> deleteQuestionById(@PathVariable Long id) {
        return questionService.deleteQuestionById(id);
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<?> updateQuestion(@RequestBody QuestionDTO question) {
        return questionService.updateQuestion(question);
    }

    @PostMapping("/selectQuestions")
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    public Result<?> selectAll(@RequestBody QuestionSelectDTO questionSelectDTO){
        return questionService.selectAll(questionSelectDTO);
    }

    // 根据问题id数组获取所有问题的基本信息
    @PostMapping("/getQuestionInfoByIds")
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    public Result<?> getQuestionInfoByIds(@RequestBody List<Long> questionIds) {
        return questionService.getQuestionInfoByIds(questionIds,false);
    }

    //根据examRecordId获取该考试的所有问题的基本信息
    @GetMapping("/getQuestionInfoByExamRecordId/{examRecordId}")
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    public Result<?> getQuestionInfoByExamRecordId(@PathVariable Long examRecordId) {
        return questionService.getQuestionInfoByExamRecordId(examRecordId,false);
    }

    // TODO： 权限问题需要限制一下
    //根据examRecordId获取该考试的所有问题的信息，包括答案。只有当考生交卷以后能看到答案,教师阅卷，教务员查看详情也能看到
    @GetMapping("/getQuestionInfoByExamRecordIdWithCorrectAnswer/{examRecordId}")
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    public Result<?> getQuestionInfoByExamRecordIdWithCorrectAnswer(@PathVariable Long examRecordId) {
        return questionService.getQuestionInfoByExamRecordId(examRecordId,true);
    }


}
