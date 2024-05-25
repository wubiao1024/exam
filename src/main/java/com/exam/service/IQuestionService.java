package com.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.POJO.BO.QuestionBO;
import com.exam.POJO.DTO.QuestionDTO;
import com.exam.POJO.DTO.QuestionSelectDTO;
import com.exam.POJO.DTO.QuestionUpdateDTO;
import com.exam.common.Result;
import com.exam.entity.Question;

import java.util.List;

/**
 * <p>
 * 问题 服务类
 * </p>
 *
 * @author 洛克
 * @since 2024-04-23
 */
public interface IQuestionService extends IService<Question> {
    Result<?> addQuestion(QuestionDTO question, String token);

    Result<?> deleteQuestionById(Long id);

    Result<?> updateQuestion(QuestionDTO question);

    Result<?> selectAll(QuestionSelectDTO questionSelectDTO);

    Result<?> getQuestionInfoByIds(List<Long> questionIds, Boolean showAnswer);

    Result<?> getQuestionInfoByExamRecordId(Long examRecordId,Boolean showCorrectAnswer);

}
