package com.exam.controller;


import com.exam.POJO.DTO.QuestionDTO;
import com.exam.common.Result;
import com.exam.entity.Question;
import com.exam.service.impl.QuestionServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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
    QuestionServiceImpl questionService;

    @PutMapping("/addQuestion")
    public Result<?> addQuestion(@RequestBody QuestionDTO question, @RequestHeader("token") String token) {
        return questionService.addQuestion(question, token);
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteQuestionById(@PathVariable Long id) {
        return questionService.deleteQuestionById(id);
    }

    @PostMapping("/update")
    public Result<?> updateQuestion(@RequestBody Question question) {
        return questionService.updateQuestion(question);
    }

    @PostMapping("/selectAllByPage/{pageNo}/{pageSize}")
    public Result<?> selectAll(@PathVariable Integer pageNo,@PathVariable Integer pageSize){
        return questionService.selectAll(pageNo,pageSize);
    }

}
