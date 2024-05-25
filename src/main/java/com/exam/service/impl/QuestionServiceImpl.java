package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.POJO.BO.QAutoBO;
import com.exam.POJO.BO.QOperationBO;
import com.exam.POJO.BO.QuestionInfoBO;
import com.exam.POJO.DTO.QuestionDTO;
import com.exam.POJO.DTO.QuestionSelectDTO;
import com.exam.POJO.DTO.QuestionsDTO;
import com.exam.common.Result;
import com.exam.entity.*;
import com.exam.entity.myEnum.QuestionType;
import com.exam.mapper.*;
import com.exam.service.IQuestionService;
import com.exam.utils.JwtUtils;
import com.exam.utils.PageUtils;
import com.exam.utils.RedisCache;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    @Resource
    SubjectMapper subjectMapper;
    @Autowired
    private ExamRecordMapper examRecordMapper;
    @Autowired
    private PqMapper pqMapper;
    @Autowired
    private StudentAnswerMapper studentAnswerMapper;

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
        // 主观题或者客观题的id
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
    public Result<?> updateQuestion(QuestionDTO question) {
        if(question.getId() == null){
            return Result.fail("question不能为null,更新失败");
        }
        QAuto  qAuto = new QAuto();
        QOperation qOperation = new QOperation();
        // 转换对象
        if(question.getQAuto()!=null){
            BeanUtils.copyProperties(question.getQAuto(),qAuto);
        }
        if(question.getQOperation()!=null){
            BeanUtils.copyProperties(question.getQOperation(),qOperation);
        }
        // 读取
        Integer isAuto = getIsAutoByQuestionType(question.getQuestionType());
        Long questionId = question.getId();

        Question _question = new Question();
        BeanUtils.copyProperties(question,_question);
        _question.setIsAuto(getIsAutoByQuestionType(_question.getQuestionType()));

        QAuto qAutoSQL = qAutoMapper.selectOne(new LambdaQueryWrapper<QAuto>().eq(QAuto::getQId, questionId));
        QOperation qOperationSQL = qOperationMapper.selectOne(new LambdaQueryWrapper<QOperation>().eq(QOperation::getQId, questionId));
        if(isAuto == 1L){
            //如果存在 删除qOperation
            if(qOperationSQL !=null){
                qOperationMapper
                        .delete(new LambdaQueryWrapper<QOperation>()
                                .eq(QOperation::getQId,questionId));
            }

            if(qAutoSQL!=null){
                qAutoMapper.update(qAuto,
                        new LambdaUpdateWrapper<QAuto>()
                                .eq(QAuto::getQId, questionId)
                );
            }else {
                qAuto.setQId(questionId);
                qAuto.setId(redisCache.getUID());
                qAutoMapper.insert(qAuto);
            }//

        }
        else { // 说明问题被更新为qOperation
            if(qAutoSQL !=null){ // 删
                qAutoMapper
                        .delete(new LambdaQueryWrapper<QAuto>()
                                .eq(QAuto::getQId,questionId));
            }
            if(qOperationSQL!=null){
                qOperationMapper.update(qOperation,
                        new LambdaUpdateWrapper<QOperation>()
                                .eq(QOperation::getQId, questionId)
                );
            }else {
                qOperation.setQId(questionId);
                qOperation.setId(redisCache.getUID());
                qOperationMapper.insert(qOperation);
            }
        }

        // 更新问题
        questionMapper.update(_question,
                new LambdaUpdateWrapper<Question>()
                        .eq(Question::getId,questionId)
        );

        return Result.success("更新成功");
    }

    @Override
    public Result<?> selectAll(QuestionSelectDTO questionSelectDTO) {

        Long pageNo = questionSelectDTO.getPageNo();
        Long pageSize = questionSelectDTO.getPageSize();
        if(pageSize == null || pageNo == null || pageNo < 1 || pageSize < 1){
            return Result.fail("分页的参数不正确");
        }
        Page<Question> page = new Page<>(pageNo, pageSize);
        Long questionId = questionSelectDTO.getQuestionId();
        Long subjectId = questionSelectDTO.getSubjectId();
        QuestionType questionType = questionSelectDTO.getQuestionType();

        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        if(questionType!=null){
            wrapper.eq(Question::getQuestionType,questionType);
        }
        if(questionId!=null){
            wrapper.eq(Question::getId,questionId);
        }
        if(subjectId!=null){
            wrapper.eq(Question::getSubjectId,subjectId);
        }

        Page<Question> questionPage = questionMapper.selectPage(page, wrapper);

        ArrayList<QuestionDTO> questionDTOS = new ArrayList<>();

        // questions
        List<Question> questions = questionPage.getRecords();

        questions.forEach(question -> {
            QuestionDTO questionDTO = new QuestionDTO();
            Long questionId1= question.getId();
            BeanUtils.copyProperties(question,questionDTO);

            // 问题类型是主观题
            if(question.getIsAuto() == 1){
                QAuto qAuto = qAutoMapper.selectOne(new LambdaQueryWrapper<QAuto>().eq(QAuto::getQId, questionId1));
                if(qAuto!=null){
                    QAutoBO qAutoBO = new QAutoBO();
                    BeanUtils.copyProperties(qAuto,qAutoBO);
                    questionDTO.setQAuto(qAutoBO);
                }
            }else { // 客观题
                QOperation qOperation = qOperationMapper.selectOne(new LambdaQueryWrapper<QOperation>().eq(QOperation::getQId, questionId1));
                if(qOperation!=null){
                    QOperationBO qOperationBO = new QOperationBO();
                    BeanUtils.copyProperties(qOperation,qOperationBO);
                    questionDTO.setQOperation(qOperationBO);
                }
            }
            Subject subject = subjectMapper.selectOne(new LambdaQueryWrapper<Subject>().eq(Subject::getId,question.getSubjectId()));
            // subject 赋值
            if(subject!=null){
                if (subject.getName() != null) {
                    questionDTO.setSubjectName(subject.getName());
                }
                if (subject.getEnglishName() != null) {
                    questionDTO.setSubjectEnglishName(subject.getEnglishName());
                }
            }

            questionDTOS.add(questionDTO);

        });

        return Result.success(PageUtils.getResMapByPage(questionPage,"questions",questionDTOS));
    }

    @Override
    public Result<?> getQuestionInfoByIds(List<Long> questionIds, Boolean showAnswer) {
        // 非空判断
        if(questionIds == null || questionIds.isEmpty()) {
            return Result.fail("参数不能为空");
        }
        // 去重
        questionIds = questionIds.stream().distinct().collect(Collectors.toList());
        // 查询题目信息
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>().in(Question::getId, questionIds));
        // 定义map的key为questionType，value为QuestionsDTO类型的list
        HashMap<Object, Object> map = new HashMap<>();

        // 题目数量
        Integer totalCount = questions.size();
        // 题目总分
        Integer totalScore1 = questions.stream().map(Question::getQuestionScore).reduce(Integer::sum).get();
        map.put("totalCount",totalCount);
        map.put("totalScore",totalScore1);

        for (QuestionType questionType : QuestionType.values()) {
            QuestionsDTO questionsDTO = new QuestionsDTO();
            List<Question> questionList =
                    questions.stream().filter(question -> question.getQuestionType() == questionType).toList();    // 筛选出指定类型题目
            if(questionList.isEmpty()) {
                continue;
            }
            // 计算总分
            Integer totalScore = questionList.stream().map(Question::getQuestionScore).reduce(Integer::sum).get();
            questionsDTO.setTotalScore(totalScore);
            // 计算题目数量
            Integer questionCount = questionList.size();
            questionsDTO.setTotalCount(questionCount);
            // QuestionInfoBO List
            List<QuestionInfoBO> questionInfoBOList = new ArrayList<>();
            for (Question question : questionList) {
                QuestionInfoBO questionInfoBO = new QuestionInfoBO();
                BeanUtils.copyProperties(question, questionInfoBO);
                // 根据questionType
                if(question.getQuestionType() == QuestionType.PD || question.getQuestionType() == QuestionType.XZ){
                    // 在qAutoMapper中查询
                    QAuto qAuto = qAutoMapper.selectOne(new LambdaQueryWrapper<QAuto>().eq(QAuto::getQId, question.getId()));
                    //TODO
                    if(qAuto!=null){
                        if(showAnswer){
                            BeanUtils.copyProperties(qAuto, questionInfoBO, "answer");
                            questionInfoBO.setCorrectAnswer(qAuto.getAnswer());
                        }else{
                            BeanUtils.copyProperties(qAuto, questionInfoBO, "answer");
                        }
                    }
                }else {
                    // 在qOperationMapper中查询
                    QOperation qOperation = qOperationMapper.selectOne(new LambdaQueryWrapper<QOperation>().eq(QOperation::getQId, question.getId()));
                    if(qOperation!=null){
                        if(showAnswer){
                            BeanUtils.copyProperties(qOperation, questionInfoBO, "answer");
                            questionInfoBO.setCorrectAnswer(qOperation.getAnswer());
                        }else{
                            BeanUtils.copyProperties(qOperation, questionInfoBO, "answer");
                        }
                    }
                }
                questionInfoBO.setQuestionId(question.getId());
                questionInfoBOList.add(questionInfoBO);
            }
            questionsDTO.setContentList(questionInfoBOList);
            // 放入map中
            map.put(questionType, questionsDTO);
        }
        return Result.success(map);
    }

    @Override
    public Result<?> getQuestionInfoByExamRecordId(Long examRecordId,Boolean showCorrectAnswer) {
        if(examRecordId == null) {
            return Result.fail("参数不能为空");
        }
        ExamRecord examRecord = examRecordMapper.selectOne(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getId,
                examRecordId));
        Long paperId = examRecord.getPaperId();
        if(paperId == null){
            return Result.fail("试卷不存在");
        }
        Integer status = examRecord.getStatus();
        if(status != 2 && !showCorrectAnswer){
            return Result.fail("考试已经结束，无法继续作答!");
        }
        List<Long> questionIds = pqMapper.selectList(new LambdaQueryWrapper<Pq>().select(Pq::getQuestionId).eq(Pq::getPaperId, paperId))
                .stream()
                .map(Pq::getQuestionId).toList();
        if(questionIds.isEmpty()){
            return Result.fail("试卷中没有题目");
        }

        if(status == 0){
            return Result.fail("您没有参加考试，不能查看详情！");
        }

        // 去重
        questionIds = questionIds.stream().distinct().collect(Collectors.toList());
        // 查询题目信息
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>().in(Question::getId, questionIds));
        // 定义map的key为questionType，value为QuestionsDTO类型的list
        HashMap<Object, Object> map = new HashMap<>();

        // 题目数量
        Integer totalCount = questions.size();
        // 题目总分
        Integer totalScore1 = questions.stream().map(Question::getQuestionScore).reduce(Integer::sum).get();
        map.put("totalCount",totalCount);
        map.put("totalScore",totalScore1);

        for (QuestionType questionType : QuestionType.values()) {
            QuestionsDTO questionsDTO = new QuestionsDTO();
            List<Question> questionList =
                    questions.stream().filter(question -> question.getQuestionType() == questionType).toList();    // 筛选出指定类型题目
            if(questionList.isEmpty()) {
                continue;
            }
            // 计算总分
            Integer totalScore = questionList.stream().map(Question::getQuestionScore).reduce(Integer::sum).get();
            questionsDTO.setTotalScore(totalScore);
            // 计算题目数量
            Integer questionCount = questionList.size();
            questionsDTO.setTotalCount(questionCount);
            // QuestionInfoBO List
            List<QuestionInfoBO> questionInfoBOList = new ArrayList<>();
            for (Question question : questionList) {
                QuestionInfoBO questionInfoBO = new QuestionInfoBO();
                BeanUtils.copyProperties(question, questionInfoBO);
                // 根据questionType
                if(question.getQuestionType() == QuestionType.PD || question.getQuestionType() == QuestionType.XZ){
                    // 在qAutoMapper中查询
                    QAuto qAuto = qAutoMapper.selectOne(new LambdaQueryWrapper<QAuto>().eq(QAuto::getQId, question.getId()));
                    // 设置问题id
                    questionInfoBO.setQuestionId(question.getId());

                    if(qAuto!=null){
                        BeanUtils.copyProperties(qAuto, questionInfoBO, "answer");
                        if(showCorrectAnswer && status != 1 && status != 2){
                            questionInfoBO.setCorrectAnswer(qAuto.getAnswer());
                        }
                        // 在studentAnswer中找答案
                        Long studentId = examRecord.getStudentId();
                        StudentAnswer studentAnswer = studentAnswerMapper.selectOne(new LambdaQueryWrapper<StudentAnswer>()
                                .eq(StudentAnswer::getStudentId, studentId)
                                .eq(StudentAnswer::getExamRecordId, examRecordId)
                                .eq(StudentAnswer::getQuestionId, question.getId())
                        );
                        if(studentAnswer != null){
                            questionInfoBO.setAnswer(studentAnswer.getAnswer());
                        }
                    }
                }else {
                    // 在qOperationMapper中查询
                    QOperation qOperation = qOperationMapper.selectOne(new LambdaQueryWrapper<QOperation>().eq(QOperation::getQId, question.getId()));
                    if(qOperation!=null){
                        BeanUtils.copyProperties(qOperation, questionInfoBO, "answer");
                        if(showCorrectAnswer && status != 1 && status != 2){
                            questionInfoBO.setCorrectAnswer(qOperation.getAnswer());
                        }
                        Long studentId = examRecord.getStudentId();
                        StudentAnswer studentAnswer = studentAnswerMapper.selectOne(new LambdaQueryWrapper<StudentAnswer>()
                                .eq(StudentAnswer::getStudentId, studentId)
                                .eq(StudentAnswer::getExamRecordId, examRecordId)
                                .eq(StudentAnswer::getQuestionId, question.getId())
                        );
                        if(studentAnswer != null){
                            questionInfoBO.setScore(studentAnswer.getScore());
                            questionInfoBO.setAnswer(studentAnswer.getAnswer());
                            questionInfoBO.setComment(studentAnswer.getComment());
                        }

                    }
                }
                questionInfoBO.setQuestionId(question.getId());
                questionInfoBOList.add(questionInfoBO);
            }
            questionsDTO.setContentList(questionInfoBOList);
            // 放入map中
            map.put(questionType, questionsDTO);
        }
        return Result.success(map);
    }

}
