package com.exam.service;

import com.exam.POJO.DTO.MarkQuestionDTO;
import com.exam.POJO.DTO.SaveAnswerDTO;
import com.exam.common.Result;
import com.exam.entity.StudentAnswer;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 学生-答题表 服务类
 * </p>
 *
 * @author 洛克
 * @since 2024-04-21
 */
public interface IStudentAnswerService extends IService<StudentAnswer> {

    Result<?> generatorRecords(Long id);

    Result<?> savaAnswer(SaveAnswerDTO answerDTO);

    Result<?> markQuestion(MarkQuestionDTO markQuestionDTO);
}


