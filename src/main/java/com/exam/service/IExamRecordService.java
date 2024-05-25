package com.exam.service;

import com.exam.POJO.DTO.PaperSelectDTO;
import com.exam.POJO.DTO.PublishExamDTO;
import com.exam.common.Result;
import com.exam.entity.ExamRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 洛克
 * @since 2024-04-21
 */
public interface IExamRecordService extends IService<ExamRecord> {

    Result<String> publishExam(PublishExamDTO publishExamDTO);


    Result<?> getExamRecords(String token);

    Result<?> getExamRecordById(Long id);

    Result<String> submitExam(Long examRecordId);

    Result<Boolean> isExamFinished(Long examRecordId);

    Result<String> readPreamble(Long examRecordId);

    Result<?> getIsReadPreamble(Long examRecordId);

    Result<?> getHistoryExamRecords(String token, PaperSelectDTO selectDTO);

    Result<?> getMarkExamRecords(String token, PaperSelectDTO selectDTO);

    Result<String> markFinished(Long examRecordId);

    Result<?> getStatisticsExamRecords(String token, PaperSelectDTO selectDTO);
}
