package com.exam.controller;


import com.exam.POJO.DTO.MarkQuestionDTO;
import com.exam.POJO.DTO.SaveAnswerDTO;
import com.exam.common.Result;
import com.exam.service.IStudentAnswerService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 学生-答题表 前端控制器
 * </p>
 *
 * @author 洛克
 * @since 2024-04-21
 */
@RestController
@RequestMapping("/api/studentAnswer")
public class StudentAnswerController {
    @Resource private IStudentAnswerService studentAnswerService;
    /**
     * 生成答题记录
     * @param id examRecordId
     * @return Result
     */
    @GetMapping("/generatorRecords/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<?> generatorRecords(@PathVariable("id") Long id) {
        return studentAnswerService.generatorRecords(id);
    }

    /**
     * 考生答题
     */
    @PostMapping("/saveAnswer")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<?> savaAnswer(@RequestBody SaveAnswerDTO answerDTO) {
        return studentAnswerService.savaAnswer(answerDTO);
    }

    /**
     *  教师情分
     */
    @PostMapping("/markQuestion")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<?> markQuestion(@RequestBody MarkQuestionDTO markQuestionDTO) {
        return studentAnswerService.markQuestion(markQuestionDTO);
    }


}
