package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.POJO.BO.QAutoBO;
import com.exam.POJO.BO.QOperationBO;
import com.exam.POJO.DTO.QuestionDTO;
import com.exam.common.Result;
import com.exam.entity.QAuto;
import com.exam.entity.QOperation;
import com.exam.entity.Question;
import com.exam.entity.myEnum.QuestionType;
import com.exam.mapper.QAutoMapper;
import com.exam.mapper.QOperationMapper;
import com.exam.mapper.QuestionMapper;
import com.exam.service.IQuestionService;
import com.exam.utils.JwtUtils;
import com.exam.utils.RedisCache;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 问题 服务实现类
 * </p>
 *
 * @author 洛克
 * @since 2024-04-23
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements IQuestionService {
    @Resource
    QuestionMapper questionMapper;
    @Resource
    QAutoMapper qAutoMapper;
    @Resource
    QOperationMapper qOperationMapper;

    @Resource
    RedisCache redisCache;

    public static Integer getIsAutoByQuestionType(QuestionType questionType) {
        if (questionType.equals(QuestionType.XZ) || questionType.equals(QuestionType.PD)) {
            return 1;
        }
        return 0;
    }

       @Override
        public Result<?> addQuestion(QuestionDTO questionDTO, String token) {
        QuestionType questionType = questionDTO.getQuestionType();
        Integer isAuto = getIsAutoByQuestionType(questionType);
        Long questionId = redisCache.getUID();
        Long otherQuestionId = redisCache.getUID();
        QAuto qAuto;
        QOperation qOperation;
        if (isAuto == 1L) {
            QAutoBO qAutoBO = questionDTO.getQAuto();
            if (qAutoBO==null) {
                return Result.fail("问题类型" + questionType + "不正确");

            }
            qAuto = new QAuto(qAutoBO);
            // 判断题目是否存在
            LambdaQueryWrapper<QAuto> wrapper = new LambdaQueryWrapper<>();
            wrapper
                    .eq(QAuto::getDescription,qAuto.getDescription())
                    .eq(QAuto::getAnswer,qAuto.getAnswer())
                    .eq(QAuto::getA,qAuto.getA())
                    .eq(QAuto::getB,qAuto.getB())
                    .eq(QAuto::getC,qAuto.getC())
                    .eq(QAuto::getD,qAuto.getD());
            QAuto sqlEntity = qAutoMapper.selectOne(wrapper);
            if(sqlEntity!=null){
                return Result.fail("已经存在这道题了");
            }

            qAuto.setId(otherQuestionId);
            qAuto.setQId(questionId);

            qAutoMapper.insert(qAuto);
        } else {
            QOperationBO qOperationBO = questionDTO.getQOperation();
            if (qOperationBO == null) {
                return Result.fail("问题类型" + questionType + "不正确");
            }
            qOperation = new QOperation(qOperationBO);
            // 如果已经存在，不再插入
            LambdaQueryWrapper<QOperation> wrapper = new LambdaQueryWrapper<>();
            wrapper
                    .eq(QOperation::getAnswer, qOperation.getAnswer())
                    .eq(QOperation::getDescription, qOperation.getDescription());
            QOperation sqlEntity = qOperationMapper.selectOne(wrapper);
            if (sqlEntity!=null) {
                return Result.fail("已经存在这道题了");
            }
            qOperation.setId(otherQuestionId);
            qOperation.setQId(questionId);
            qOperationMapper.insert(qOperation);
        }
        // 处理QuestionDTO===>Question
        Question question = new Question(questionDTO);
        question.setId(questionId);
        Long creatorId = JwtUtils.getIdByToken(token);
        question.setCreatorId(creatorId);
        question.setIsAuto(isAuto);

        questionMapper.insert(question);
        return Result.success("添加成功");
    }

    @Override
    public Result<?> deleteQuestionById(Long id) {
        /*LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(Question::getId,id);*/
        Question question = questionMapper.selectOne(
                new LambdaQueryWrapper<Question>()
                        .eq(Question::getId,id)
        );
        if (question == null) {
            return Result.fail("问题不存在");
        }
        Integer isAuto = question.getIsAuto();
        if (isAuto == 1) {
            qAutoMapper.delete(
                    new LambdaQueryWrapper<QAuto>()
                            .eq(QAuto::getQId,id)
            );
        } else {
            qOperationMapper.delete(
                    new LambdaQueryWrapper<QOperation>()
                            .eq(QOperation::getQId,id));
        }
        questionMapper.delete(
                new LambdaQueryWrapper<Question>()
                        .eq(Question::getId,id)
        );
        return Result.success("删除成功");
    }

    @Override
    public Result<?> updateQuestion(Question question) {
        if(question.getId() == null){
            return Result.fail("question不能为null,更新失败");
        }
        QAuto qAuto = question.getQAuto();
        QOperation qOperation = question.getQOperation();
        Integer isAuto = getIsAutoByQuestionType(question.getQuestionType());
        Long questionId = question.getId();
        if(isAuto == 1L){
            qAutoMapper.update(qAuto,
                    new LambdaUpdateWrapper<QAuto>()
                            .eq(QAuto::getQId, questionId)
            );
        }else {
            qOperationMapper.update(qOperation,
                    new LambdaUpdateWrapper<QOperation>()
                            .eq(QOperation::getQId, questionId)
                    );
        }
        questionMapper.update(question,
                new LambdaUpdateWrapper<Question>()
                        .eq(Question::getId,questionId)
        );

        return Result.success("更新成功");
    }

    @Override
    public Result<?> selectAll(Integer pageNo, Integer pageSize) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Question::getId,Question::getQuestionType,Question::getSubjectId,Question::getLevel);
//        new Page<>(Long.pa);
        //        Optional.ofNullable()

        List<Map<String, Object>> maps = questionMapper.selectMaps(wrapper);
        return null;
    }
}
