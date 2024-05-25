package com.exam.controller;


import com.exam.POJO.DTO.PaperDTO;
import com.exam.POJO.DTO.PaperSelectDTO;
import com.exam.common.Result;
import com.exam.service.impl.PaperServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 试卷 前端控制器
 * </p>
 *
 * @author 洛克
 * @since 2024-04-21
 */
@RestController
@RequestMapping("/api/paper")
public class PaperController {
    @Resource
    private PaperServiceImpl paperService;

    @GetMapping("/getPaperById/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public Result<?> getPaperById(@PathVariable("id") Long id) {
        return paperService.getPaperById(id);
    }

    @PutMapping("/add")
    @PreAuthorize("hasAnyRole('TEACHER')")
    public Result<?> addPaper(@RequestBody PaperDTO paperDTO, @RequestHeader String token) {
        return paperService.addPaper(paperDTO,token);
    }

    @PostMapping("/getPapers")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public Result<?> getPapers(@RequestBody PaperSelectDTO paperSelectDTO) {
        return paperService.findPapers(paperSelectDTO);
    }

    // TODO DELETE 删除时对其他表的影响
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<?> deletePaper(@PathVariable("id") Long id) {
        return paperService.deletePaper(id);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<?> updatePaper(@RequestBody PaperDTO paperDTO, @RequestHeader String token) {
        return paperService.updatePaper(paperDTO,token);
    }

    // 根据questionIds获取问题总分
    @PostMapping("/getQuestionTotalScore")
    @PreAuthorize("hasAnyRole('TEACHER','STUDENT','ADMIN')")
    public Result<?> getQuestionTotalScore(@RequestBody  List<Long> questionIds) {
       return paperService.getQuestionTotalScore(questionIds);
    }


}
