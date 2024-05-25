package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.exam.POJO.DTO.MarkQuestionDTO;
import com.exam.POJO.DTO.SaveAnswerDTO;
import com.exam.common.Result;
import com.exam.entity.ExamRecord;
import com.exam.entity.Pq;
import com.exam.entity.StudentAnswer;
import com.exam.mapper.ExamRecordMapper;
import com.exam.mapper.PqMapper;
import com.exam.mapper.StudentAnswerMapper;
import com.exam.service.IStudentAnswerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.utils.RedisCache;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 学生-答题表 服务实现类
 * </p>
 *
 * @author 洛克
 * @since 2024-04-21
 */
@Service
public class StudentAnswerServiceImpl extends ServiceImpl<StudentAnswerMapper, StudentAnswer> implements IStudentAnswerService {


    @Resource
    private ExamRecordMapper examRecordMapper;
    @Resource
    private PqMapper pqMapper;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private StudentAnswerMapper studentAnswerMapper;

    public StudentAnswerServiceImpl(ExamRecordMapper examRecordMapper) {
        this.examRecordMapper = examRecordMapper;
    }

    @Override
    public Result<?> generatorRecords(Long id) {
        ExamRecord examRecord = examRecordMapper.selectOne(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getId, id));
        if(examRecord == null){
            return Result.fail("考试记录不存在");
        }
        if(examRecord.getStatus() > 2){
            return Result.fail("考试已经结束");
        }
        if(examRecord.getStatus() == 2){
            return Result.success("答题记录已生成！");
        }

        if(examRecord.getStatus() == 1){
            examRecordMapper.update(
                    new LambdaUpdateWrapper<ExamRecord>()
                            .eq(ExamRecord::getId, id)
                            .set(ExamRecord::getStatus, 2)
                            .set(ExamRecord::getEnterTime, LocalDateTime.now())
            );
        }



        //1. 先查询出考试的题目
        List<Long> list = pqMapper.selectList(new LambdaQueryWrapper<Pq>().eq(Pq::getPaperId, examRecord.getPaperId()))
                .stream()
                .map(Pq::getQuestionId)
                .toList();
        Long studentId = examRecord.getStudentId();
        Long examRecordId = examRecord.getId();

        //2. 然后根据题目生成答题记录
        list.forEach(questionId -> {
            StudentAnswer studentAnswer = new StudentAnswer();
            studentAnswer.setExamRecordId(examRecordId);
            studentAnswer.setQuestionId(questionId);
            studentAnswer.setStudentId(studentId);
            studentAnswer.setId(redisCache.getUID());
            // 插入表
            studentAnswerMapper.insert(studentAnswer);
        });

        return Result.success("答题记录生成成功！");
    }

    @Override
    public Result<?> savaAnswer(SaveAnswerDTO answerDTO) {
        if(answerDTO == null){
            return Result.fail("答案保存失败，参数错误");
        }
        Long examRecordId = answerDTO.getExamRecordId();
        ExamRecord examRecord = examRecordMapper.selectOne(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getId, examRecordId));
        if(examRecord == null){
            return Result.fail("答案保存失败，考试记录不存在");
        }

 /*       StudentAnswer studentAnswer = studentAnswerMapper.selectOne(new LambdaQueryWrapper<StudentAnswer>()
                .eq(StudentAnswer::getQuestionId, answerDTO.getQuestionId())
                .eq(StudentAnswer::getStudentId, examRecord.getStudentId())
                .eq(StudentAnswer::getExamRecordId, examRecordId)
        );*/
        //更新
        studentAnswerMapper.update(new LambdaUpdateWrapper<StudentAnswer>()
                .eq(StudentAnswer::getQuestionId, answerDTO.getQuestionId())
                .eq(StudentAnswer::getStudentId, examRecord.getStudentId())
                .eq(StudentAnswer::getExamRecordId, examRecordId)
                .set(StudentAnswer::getAnswer, answerDTO.getAnswer()));

        return Result.success("答案保存成功！");
    }

    @Override
    public Result<?> markQuestion(MarkQuestionDTO markQuestionDTO) {
        if(markQuestionDTO == null){
            return Result.fail("批阅失败，请求参数错误");
        }
        Long examRecordId = markQuestionDTO.getExamRecordId();
        ExamRecord examRecord = examRecordMapper.selectOne(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getId, examRecordId));
        if(examRecord == null){
            return Result.fail("批阅失败，考试记录不存在");
        }

        studentAnswerMapper.update(new LambdaUpdateWrapper<StudentAnswer>()
                .eq(StudentAnswer::getQuestionId, markQuestionDTO.getQuestionId())
                .eq(StudentAnswer::getStudentId, examRecord.getStudentId())
                .eq(StudentAnswer::getExamRecordId, examRecordId)
                .set(StudentAnswer::getScore, markQuestionDTO.getScore())
                .set(StudentAnswer::getComment,markQuestionDTO.getComment())

        );
        return Result.success("批阅成功！");
    }
}
