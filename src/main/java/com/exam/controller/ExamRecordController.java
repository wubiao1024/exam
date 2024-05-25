package com.exam.controller;


import com.exam.POJO.DTO.PaperSelectDTO;
import com.exam.POJO.DTO.PublishExamDTO;
import com.exam.common.Result;
import com.exam.entity.User;
import com.exam.service.IExamRecordService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 洛克
 * @since 2024-04-21
 */
@RestController
@RequestMapping("/api/examRecord")
public class ExamRecordController {
    @Resource
    IExamRecordService examRecordService;
    @PostMapping("/publishExam")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<String> publishExam(@RequestBody PublishExamDTO publishExamDTO) {
        return examRecordService.publishExam(publishExamDTO);
    }
    @GetMapping("/getExamRecords")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<?> getExamRecords(@RequestHeader("token") String token) {
        return examRecordService.getExamRecords(token);
    }

    @PostMapping("/getHistoryExamRecords")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<?>getHistoryExamRecords (@RequestHeader("token") String token, @RequestBody PaperSelectDTO selectDTO) {
        return examRecordService.getHistoryExamRecords(token,selectDTO);
    }

    // 教师阅卷的时候读取试卷
    @PostMapping("/getMarkExamRecords")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<?>getMarkExamRecords (@RequestHeader("token") String token, @RequestBody PaperSelectDTO selectDTO) {
        return examRecordService.getMarkExamRecords(token,selectDTO);
    }

    // 教务员成绩统计时候读取试卷
    @PostMapping("/getStatisticsExamRecords")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?>getStatisticsExamRecords (@RequestHeader("token") String token, @RequestBody PaperSelectDTO selectDTO) {
        return examRecordService.getStatisticsExamRecords(token,selectDTO);
    }

    @GetMapping("/getExamRecordTODOById/{id}")
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    public Result<?> getExamRecordById( @PathVariable Long id) {
        return examRecordService.getExamRecordById(id);
    }

    // 交卷
    @PostMapping("/submitExam/{examRecordId}")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<String> submitExam(@PathVariable Long examRecordId) {
        return examRecordService.submitExam(examRecordId);
    }

    //判断考试是否结束
    @GetMapping("/isExamFinished/{examRecordId}")
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    public Result<Boolean> isExamFinished(@PathVariable Long examRecordId) {
        return examRecordService.isExamFinished(examRecordId);
    }

    //阅读考试须知
    @GetMapping("/readPreamble/{examRecordId}")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public Result<String> readPreamble(@PathVariable Long examRecordId) {
        return examRecordService.readPreamble(examRecordId);
    }

    @GetMapping("/getIsReadPreamble/{examRecordId}")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public Result<?> getIsReadPreamble(@PathVariable Long examRecordId) {
        return examRecordService.getIsReadPreamble(examRecordId);
    }

    // 完成阅卷
    @GetMapping("/markFinished/{examRecordId}")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<String> markFinished(@PathVariable Long examRecordId) {
        return examRecordService.markFinished(examRecordId);
    }


}
