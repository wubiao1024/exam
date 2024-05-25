package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.POJO.DTO.*;
import com.exam.common.Result;
import com.exam.entity.*;
import com.exam.entity.Class;
import com.exam.mapper.*;
import com.exam.service.IExamRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.utils.JwtUtils;
import com.exam.utils.PageUtils;
import com.exam.utils.RedisCache;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 洛克
 * @since 2024-04-21
 */
@Service
public class ExamRecordServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements IExamRecordService {
    @Resource
    RedisCache redisCache;
    @Resource
    ExamRecordMapper examRecordMapper;
    @Resource
    private PaperMapper paperMapper;
    @Autowired
    private SubjectMapper subjectMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private ClassMapper classMapper;
    @Autowired
    private StudentAnswerMapper studentAnswerMapper;
    @Qualifier("questionMapper")
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QAutoMapper qAutoMapper;
    @Autowired
    private PqMapper pqMapper;

    @Override
    public Result<String> publishExam(PublishExamDTO publishExamDTO) {
        Long paperId = publishExamDTO.getPaperId();
        if(paperId ==null){
            return Result.fail("试卷id不能为空");
        }
        List<Long> studentIds = publishExamDTO.getStudentIds();
        if(studentIds.isEmpty()){
            return Result.fail("学生id不能为空");
        }
        //TODO 发布考试
        studentIds.forEach(studentId -> {
            ExamRecord examRecord = new ExamRecord();
            BeanUtils.copyProperties(publishExamDTO, examRecord);
            String startTime = publishExamDTO.getStartTime();
            LocalDateTime _startTime = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            examRecord.setStartTime(_startTime);
            examRecord.setStudentId(studentId);
            examRecord.setId(redisCache.getUID());
            examRecordMapper.insert(examRecord);
            // 考试开始时间
        });
        return Result.success("发布成功");
    }

    @Override
    public Result<?> getExamRecords(String token) {

        Long studentId = JwtUtils.getIdByToken(token);
        updateExamRecordStatus(studentId);
        List<ExamRecord> examRecordsTODO =
                examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getStudentId,
                        studentId).in(ExamRecord::getStatus, 1,2));

        List<ExamRecord> examRecordsHistory =
                examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getStudentId,
                        studentId).notIn(ExamRecord::getStatus, 1,2));

        List<ExamRecordsTODO> examRecordTODOList = examRecordsTODO.stream().map(this::getExamRecordTODO).toList();

        List<ExamRecordsHistory> examRecordHistoryList = examRecordsHistory.stream().map(this::getExamRecordsHistory).toList();

        //res
        ExamRecordsDTO examRecordsDTO = new ExamRecordsDTO();
        examRecordsDTO.setExamRecordsTODOList(examRecordTODOList);
        examRecordsDTO.setExamRecordsHistory(examRecordHistoryList);

        return Result.success(examRecordsDTO);
    }

    @Override
    public Result<?> getExamRecordById(Long id) {
        if(id ==null){
            return Result.fail("id不能为空");
        }
        ExamRecord examRecord = examRecordMapper.selectOne(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getId, id));
        ExamRecordsTODO examRecordTODO = getExamRecordTODO(examRecord);
        return Result.success(examRecordTODO);
    }

    @Override
    public Result<String> submitExam(Long examRecordId) {
        if(examRecordId ==null){
            return Result.fail("考试记录id不能为空");
        }
        LocalDateTime now = LocalDateTime.now();
        examRecordMapper.update(new LambdaUpdateWrapper<ExamRecord>()
                .eq(ExamRecord::getId, examRecordId)
                .set(ExamRecord::getStatus, 3)
                .set(ExamRecord::getSubmitTime, now)
        );
        return Result.success("交卷成功");
    }

    @Override
    public Result<Boolean> isExamFinished(Long examRecordId) {
        return Result.success(examRecordIsEnd(examRecordId));
    }

    @Override
    public Result<String> readPreamble(Long examRecordId) {
        if(examRecordId ==null){
            return Result.fail("考试记录id不能为空");
        }
        examRecordMapper.update(new LambdaUpdateWrapper<ExamRecord>().eq(ExamRecord::getId,
                examRecordId).set(ExamRecord::getIsRead, 1));
        return Result.success("成功");

    }

    @Override
    public Result<?> getIsReadPreamble(Long examRecordId) {
        if(examRecordId ==null){
            return Result.fail("考试记录id不能为空");
        }
        Integer isRead = examRecordMapper.selectOne(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getId, examRecordId).select(ExamRecord::getIsRead)).getIsRead();
        return Result.success(isRead == 1);
    }
    @Override
    public Result<String> markFinished(Long examRecordId) {
        if(examRecordId ==null){
            return Result.fail("考试记录id不能为空");
        }
        Integer status = examRecordMapper.selectOne(new LambdaQueryWrapper<ExamRecord>().select(ExamRecord::getStatus).eq(ExamRecord::getId,
                examRecordId)).getStatus();
        if(status == 3){
            // 自动判分
            this.autoMark(examRecordId);
        }
        examRecordMapper.update(new LambdaUpdateWrapper<ExamRecord>()
                .eq(ExamRecord::getId, examRecordId)
                .set(ExamRecord::getStatus, 4)
        );

        // 计算examRecord的总分
        studentAnswerMapper.selectList(new LambdaQueryWrapper<StudentAnswer>()
                        .select(StudentAnswer::getScore)
                        .eq(StudentAnswer::getExamRecordId, examRecordId)
                )
                .stream()
                .map(StudentAnswer::getScore)
                .reduce(Integer::sum)
                .ifPresent(totalScore -> {
                    examRecordMapper.update(new LambdaUpdateWrapper<ExamRecord>()
                            .eq(ExamRecord::getId, examRecordId)
                            .set(ExamRecord::getScore, totalScore));
                });
        // 计算得分率
        updateCorrectRate(examRecordId);
        return Result.success("批阅完成！");
    }

    @Override
    public Result<?> getHistoryExamRecords(String token, PaperSelectDTO selectDTO) {
        Long studentId = JwtUtils.getIdByToken(token);
        updateExamRecordStatus(studentId);
        if(selectDTO==null){
            return Result.fail("参数不能为空");
        }
        // 找到所有满足学科限制的试卷id
        Long subjectId = selectDTO.getSubjectId();
        List<Long> paperIdList = paperMapper.selectList(new LambdaQueryWrapper<Paper>().select(Paper::getId).eq(Paper::getSubjectId, subjectId))
                .stream()
                .map(Paper::getId)
                .toList();

        Page<ExamRecord> examRecordPage = new Page<>(selectDTO.getPageNo(), selectDTO.getPageSize());
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(ExamRecord::getStudentId, studentId)
                .notIn(ExamRecord::getStatus, 1,2);

        if(subjectId !=null && !paperIdList.isEmpty()){
            wrapper.in(ExamRecord::getPaperId, paperIdList);
        }
        if(selectDTO.getStatus() !=null){
            wrapper.eq(ExamRecord::getStatus, selectDTO.getStatus());
        }

        Page<ExamRecord> resPage = examRecordMapper.selectPage(examRecordPage, wrapper);

        List<ExamRecord> records = resPage.getRecords();

        List<ExamRecordsHistory> examRecordsHistoryList = records.stream().map(this::getExamRecordsHistory).toList();

        HashMap<String, Object> res = PageUtils.getResMapByPage(resPage, "examRecordsHistoryList", examRecordsHistoryList);
        return Result.success(res);
    }


    // 统计信息
    @Override
    public Result<?> getStatisticsExamRecords(String token, PaperSelectDTO selectDTO) {
        Page<ExamRecord> resPage = getExamRecordsWithStatusList(token, selectDTO, List.of(0, 4));
        if(resPage==null){
            return Result.fail("请求失败，检查数据是否正确");
        }
        List<ExamRecord> records = resPage.getRecords();
        // 得分率计算
        records.stream().map(ExamRecord::getId).forEach(this::updateCorrectRate);

        if(records.isEmpty()){
            return Result.success("暂无记录");
        }
        List<ExamRecordsHistory> examRecordsHistoryList =
                records.stream().map(this::getExamRecordsHistory).toList();

        HashMap<String, Object> res = PageUtils.getResMapByPage(resPage, "examRecordsHistoryList", examRecordsHistoryList);
        return Result.success(res);
    }


    // 教师只能查到自己创建的考试，自己批改
    @Override
    public Result<?> getMarkExamRecords(String token, PaperSelectDTO selectDTO) {
        Page<ExamRecord> resPage = getExamRecordsWithStatusList(token, selectDTO, List.of(3, 4));
        if(resPage.getRecords().isEmpty()){
            return Result.fail("暂无记录");
        }
        List<ExamRecordsHistory> examRecordsHistoryList =
                resPage.getRecords().stream().map(this::getExamRecordsHistory).toList();

        HashMap<String, Object> res = PageUtils.getResMapByPage(resPage, "examRecordsHistoryList", examRecordsHistoryList);
        return Result.success(res);
    }

    //TODO 教师只能查到自己创建的考试，自己批改,有待改进，根据token查看身份再决定
    public Page<ExamRecord> getExamRecordsWithStatusList(String token, PaperSelectDTO selectDTO, List<Integer> statusList) {
        updateAllExamRecordsStatus();
        if(selectDTO==null){
            return null;
        }

        List<Long> studentIds = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getRoleId, 1L)
                .select(UserRole::getUserId)
        ).stream().map(UserRole::getUserId).toList();

        //  根据学生姓名查询学生 模糊查询 studentName, classId
        LambdaQueryWrapper<User> userWrap = new LambdaQueryWrapper<User>();
        userWrap
                .select(User::getId)
                .in(User::getId, studentIds);
        if(selectDTO.getStudentName() !=null){
            userWrap.like(User::getUsername, selectDTO.getStudentName());
        }
        if(selectDTO.getClassId()!=null){
            userWrap.eq(User::getClassId, selectDTO.getClassId());
        }

        List<Long> studentIdList = userMapper.selectList(userWrap)
                .stream()
                .map(User::getId)
                .toList();

        // 找到所有满足学科限制的试卷id subjectId
        Long subjectId = selectDTO.getSubjectId();
        List<Long> paperIdList = paperMapper.selectList(new LambdaQueryWrapper<Paper>()
                        .select(Paper::getId)
                        .eq(Paper::getSubjectId, subjectId))
                .stream()
                .map(Paper::getId)
                .toList();

        Page<ExamRecord> examRecordPage = new Page<>(selectDTO.getPageNo(), selectDTO.getPageSize());
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        if(!studentIdList.isEmpty()){
            wrapper.in(ExamRecord::getStudentId, studentIdList);
        }
        Integer status = selectDTO.getStatus();
        if(subjectId !=null && !paperIdList.isEmpty()){
            wrapper.in(ExamRecord::getPaperId, paperIdList);
        }
        if(status !=null){
            wrapper.eq(ExamRecord::getStatus, status);
        }
        wrapper.in(ExamRecord::getStatus, statusList);
        return examRecordMapper.selectPage(examRecordPage, wrapper);


    }


    /**
     * 自动批阅 选择和判断题
     * @param examRecordId 考试的id
     */
    public void  autoMark(Long examRecordId){
        // 自动批阅选择题和判断题
        // 正确答案的位置 ： qAuto  ---> questionId ----> studentAnswer ---> examRecordId
        // 学生答案的位置： studentAnswer
        // 题目分数的位置：question

        // 得分率： 学生答案与正确答案相同的数量 / 总题数

        // 获取学生答案
        List<StudentAnswer> studentAnswers = studentAnswerMapper
                .selectList(new LambdaQueryWrapper<StudentAnswer>()
                        .eq(StudentAnswer::getExamRecordId, examRecordId));

        // 获取所有选择题和判断题
        List<Long> questionIds = studentAnswers.stream().map(StudentAnswer::getQuestionId).toList();
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>().eq(Question::getIsAuto, 1).in(Question::getId,
                questionIds));

        // 遍历所有选择题和判断题
        questions.forEach(question -> {
            studentAnswers.forEach(studentAnswer -> {
                if(studentAnswer.getQuestionId().equals(question.getId())){
                    // 根据questionId找到正确答案
                    String correctAnswer =
                            qAutoMapper.selectOne(new LambdaQueryWrapper<QAuto>().select(QAuto::getAnswer).eq(QAuto::getQId,
                            question.getId())).getAnswer();
                    String answer = studentAnswer.getAnswer();
                    if(answer !=null &&answer.trim().equals(correctAnswer.trim())){
                        studentAnswer.setScore(question.getQuestionScore());
                    }else {
                        studentAnswer.setScore(0);
                    }
                    // 更新 studentAnswer
                    studentAnswerMapper.update(studentAnswer, new LambdaUpdateWrapper<StudentAnswer>()
                           .eq(StudentAnswer::getId, studentAnswer.getId()));
                }
            });
        });
    }

    // 计算得分率
    public void updateCorrectRate(Long examRecordId){

        // 获取学生分数不为0的数量
        long getScoreCount = studentAnswerMapper
                .selectList(new LambdaQueryWrapper<StudentAnswer>()
                        .eq(StudentAnswer::getExamRecordId, examRecordId)
                        .ne(StudentAnswer::getScore, 0)
                ).size();
        // 总题数量
        Long paperId = examRecordMapper.selectOne(new LambdaQueryWrapper<ExamRecord>().select(ExamRecord::getPaperId).eq(ExamRecord::getId
                , examRecordId)).getPaperId();

        Long totalCount = pqMapper.selectCount(new LambdaQueryWrapper<Pq>().eq(Pq::getPaperId, paperId));

        if(totalCount == 0){
            return;
        }
        // 保留两位小数 得分率: getScoreCount / totalCount

        double scoreRate = (double) getScoreCount / (double) totalCount;
        // 保留小数点后两位
        scoreRate = (double) Math.round(scoreRate * 10000) / 10000;
        examRecordMapper.update(new LambdaUpdateWrapper<ExamRecord>()
               .eq(ExamRecord::getId, examRecordId)
               .set(ExamRecord::getCorrectRate, scoreRate));
    }

    /**
     * 考试是否结束
     * @param examRecordId 考试id
     * @return true 代表考试结束，false 代表考试未结束
     */
    private boolean examRecordIsEnd(Long examRecordId){
        Integer status = examRecordMapper.selectOne(new LambdaQueryWrapper<ExamRecord>().select(ExamRecord::getStatus).eq(ExamRecord::getId, examRecordId))
                .getStatus();
        return status != 1 && status!= 2;
    }

    /**
     * 更新指定学生的考试记录状态
     * @param studentId 考生id
     */
    public void updateExamRecordStatus(Long studentId){
        List<ExamRecord> examRecordList =
                examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>()
                        .select(ExamRecord::getId,ExamRecord::getStatus,ExamRecord::getDuration, ExamRecord::getStartTime)
                        .eq(ExamRecord::getStudentId, studentId)
                );
        updateExamRecordsByList(examRecordList);
    }

    /**
     * 更新所有的考试记录状态
     */
   public void updateAllExamRecordsStatus(){
       List<ExamRecord> examRecordList =
               examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>()
                       .select(ExamRecord::getId,ExamRecord::getStatus,ExamRecord::getDuration,
                               ExamRecord::getStartTime));
       updateExamRecordsByList(examRecordList);
   }

    /**
     * 自动交卷 自动判断没有参加考试 状态更新
     * @param examRecordList 需要自动判定的考试记录列表
     */
   public void updateExamRecordsByList(List<ExamRecord> examRecordList){
       examRecordList.forEach(examRecord -> {
           // 分钟
           Integer duration = examRecord.getDuration();
           // 考试结束时间
           LocalDateTime endTime = examRecord.getStartTime().plusMinutes(duration);
           LocalDateTime now = LocalDateTime.now();
           // 如果没有参加考试，则更新状态为 0. 没有参加考试：考试时间已经过去，但是但是状态还是 status = 1
           if(now.isAfter(endTime) && examRecord.getStatus() == 1){
               examRecord.setStatus(0);
               examRecordMapper.update(examRecord, new LambdaUpdateWrapper<ExamRecord>().eq(ExamRecord::getId, examRecord.getId()));
           }
           // 参加考试(status = 2)，考试时间已经结束，但是没有主动交卷，就自动更新状态为 3
           if(now.isAfter(endTime) && examRecord.getStatus() == 2){
               examRecord.setStatus(3);
               examRecordMapper.update(examRecord, new LambdaUpdateWrapper<ExamRecord>().eq(ExamRecord::getId, examRecord.getId()));
           }
       });
   }

    /**
     * 获取考试记录TODO
     * @param examRecord 考试记录基本信息
     * @return 完整的考试信息
     */
    public ExamRecordsTODO getExamRecordTODO(ExamRecord examRecord){
        ExamRecordsTODO examRecordTODO = new ExamRecordsTODO();
        BeanUtils.copyProperties(examRecord, examRecordTODO);
        Long paperId = examRecord.getPaperId();
        Paper paper = paperMapper.selectOne(new LambdaQueryWrapper<Paper>().eq(Paper::getId, paperId));
        Long subjectId = paper.getSubjectId();
        Subject subject = subjectMapper.selectOne(new LambdaQueryWrapper<Subject>().eq(Subject::getId, subjectId));
        String subjectName = "";
        if(subject!=null){
            subjectName = subject.getName();
        }
        examRecordTODO.setSubjectName(subjectName);
        BeanUtils.copyProperties(paper, examRecordTODO,"id");
        examRecordTODO.setPaperId(paperId);
        return  examRecordTODO;
    }

    /**
     * 获取考试记录历史History
     * @param examRecord 考试记录基本信息
     * @return 完整的历史考试信息,考生姓名，班级等，更加完整
     */
    private ExamRecordsHistory getExamRecordsHistory(ExamRecord examRecord) {
        ExamRecordsHistory examRecordHistory = new ExamRecordsHistory();

        ExamRecord record = examRecordMapper.selectOne(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getId, examRecord.getId()));


        BeanUtils.copyProperties(record, examRecordHistory);
        Long paperId = record.getPaperId();
        Paper paper = paperMapper.selectOne(new LambdaQueryWrapper<Paper>().eq(Paper::getId, paperId));
        Long subjectId = paper.getSubjectId();
        Subject subject = subjectMapper.selectOne(new LambdaQueryWrapper<Subject>().eq(Subject::getId, subjectId));
        String subjectName = "";
        if (subject != null) {
            subjectName = subject.getName();
        }
        examRecordHistory.setSubjectName(subjectName);
        BeanUtils.copyProperties(paper, examRecordHistory, "id");
        examRecordHistory.setPaperId(paperId);

        //考生姓名
        User user =
                userMapper.selectOne(new LambdaQueryWrapper<User>().select(User::getRealName,User::getClassId).eq(User::getId,
                        record.getStudentId()));
        examRecordHistory.setStudentName(user.getRealName());
        // 班级
        Class aClass =
                classMapper.selectOne(new LambdaQueryWrapper<Class>().select(Class::getClassName).eq(Class::getId,
                user.getClassId()));
        examRecordHistory.setClassName(aClass.getClassName());
        return examRecordHistory;
    }

}
