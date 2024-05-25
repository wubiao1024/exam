package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.POJO.BO.PaperBO;
import com.exam.POJO.BO.QuestionInfoBO;
import com.exam.POJO.DTO.PaperDTO;
import com.exam.POJO.DTO.PaperSelectDTO;
import com.exam.POJO.DTO.QuestionsDTO;
import com.exam.common.Result;
import com.exam.entity.*;
import com.exam.entity.myEnum.QuestionType;
import com.exam.mapper.*;
import com.exam.service.IPaperService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.utils.JwtUtils;
import com.exam.utils.PageUtils;
import com.exam.utils.RedisCache;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 试卷 服务实现类ff
 * </p>
 *
 * @author 洛克
 * @since 2024-04-21
 */
@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements IPaperService {
    @Resource
    RedisCache redisCache;

    @Resource
    SubjectMapper subjectMapper;

    @Resource
    PqMapper pqMapper;

    @Resource
    PaperMapper paperMapper;

    @Resource
    QuestionMapper questionMapper;

    @Override
    public Result<?> addPaper(PaperDTO paperDTO, String token) {
        Long creatorId = JwtUtils.getIdByToken(token);

        if (paperDTO == null) {
            return Result.fail("参数不能为空");
        }

        List<Long> questionIds = paperDTO.getQuestionIds();
        if (questionIds.isEmpty()) {
            return Result.fail("试卷必须包含至少一个题目");
        }

        // 把paperDTO转成paper对象
        Paper paper = new Paper();
        BeanUtils.copyProperties(paperDTO.getPaper(), paper);
        paper.setPreamble(paperDTO.getPreamble());
        paper.setId(redisCache.getUID());
        paper.setCreatorId(creatorId);

        // 循环， 根据paper 和 questionIds 生成对应的pq关系
        Integer totalScore = 0;
        for (Long questionId : questionIds) {
            Question question;
            try {
                question = questionMapper.selectOne(new LambdaQueryWrapper<Question>().eq(Question::getId, questionId));
            } catch (Exception e) {
                return Result.fail(e.getMessage());
            }
            if (question == null) {
                return Result.fail("题目id为: " + questionId + "的问题不存在！");
            }
            if (!Objects.equals(question.getSubjectId(), paper.getSubjectId())) {
                return Result.fail("题目id为: " + questionId + "的xxx科目与试卷科目学科类型和试卷不匹配！");
            }

            Pq pq = new Pq();
            pq.setId(redisCache.getUID());
            pq.setCreatorId(creatorId);
            pq.setPaperId(paper.getId());
            pq.setCreatorId(creatorId);
            pq.setQuestionId(questionId);
            pqMapper.insert(pq);
            totalScore += question.getQuestionScore();
        }
        // 设置总分
        paper.setTotalScore(totalScore);
        paperMapper.insert(paper);
        return Result.success("试卷添加成功");
    }

    // 删除试卷
    @Override
    public Result<?> deletePaper(Long id) {
        if (id == null) {
            return Result.fail("参数不能为空");
        }
        // 删除试卷
        paperMapper.delete(new LambdaQueryWrapper<Paper>().eq(Paper::getId, id));
        // 删除pq关系
        pqMapper.delete(new LambdaQueryWrapper<Pq>().eq(Pq::getPaperId, id));
        return Result.success("试卷删除成功");
    }

    // 修改试卷
    @Override
    public Result<?> updatePaper(PaperDTO paperDTO, String token) {

        if (paperDTO == null) {
            return Result.fail("参数不能为空");
        }

        List<Long> questionIds = paperDTO.getQuestionIds();

        // 把paperDTO转成paper对象
        Paper paper = new Paper();
        BeanUtils.copyProperties(paperDTO.getPaper(), paper);
        paper.setPreamble(paperDTO.getPreamble());

        // 根据paperId 查询原有pq关系
        List<Pq> pqs = pqMapper.selectList(new LambdaQueryWrapper<Pq>().eq(Pq::getPaperId, paper.getId()));
        // 根据pqs 找到原有questionIds
        List<Long> oldQuestionIds = new ArrayList<>(pqs.stream().map(Pq::getQuestionId).toList());
        ArrayList<Long> removeDiff = new ArrayList<>(oldQuestionIds);
        ArrayList<Long> addDiff = new ArrayList<>(questionIds);

        // 找出需要删除的questionIds
        removeDiff.removeAll(questionIds);
        // 找出需要新增的questionIds
        addDiff.removeAll(oldQuestionIds);

        // 删除原有冗余pq关系
        for (Long questionId : removeDiff) {
            pqMapper.delete(new LambdaQueryWrapper<Pq>().eq(Pq::getPaperId, paper.getId()).eq(Pq::getQuestionId, questionId));
        }
        Long creatorId = JwtUtils.getIdByToken(token);
        // 新增pq关系
        for (Long questionId : addDiff) {
            Question question;
            try {
                question = questionMapper.selectOne(new LambdaQueryWrapper<Question>().eq(Question::getId, questionId));
            } catch (Exception e) {
                return Result.fail(e.getMessage());
            }
            if (question == null) {
                return Result.fail("题目id为: " + questionId + "的问题不存在！");
            }
            if (!Objects.equals(question.getSubjectId(), paper.getSubjectId())) {
                return Result.fail("题目id为: " + questionId + "的科目与试卷科目学科类型和试卷不匹配！");
            }

            Pq pq = new Pq();
            pq.setId(redisCache.getUID());
            pq.setPaperId(paper.getId());
            pq.setQuestionId(questionId);
            pq.setCreatorId(creatorId);
            pqMapper.insert(pq);
        }
        int totalScore = 0;
        // 计算总分
        for (Long questionId : questionIds) {
            Question question = questionMapper.selectOne(new LambdaQueryWrapper<Question>().eq(Question::getId, questionId));
            totalScore += question.getQuestionScore();
        }

        // 设置总分
        paper.setTotalScore(totalScore);
        paperMapper.update(paper, new LambdaQueryWrapper<Paper>().eq(Paper::getId, paper.getId()));
        return Result.success("试卷修改成功");
    }

    // 分页查询试卷
    @Override
    public Result<?> findPapers(PaperSelectDTO paperSelectDTO) {
        if (paperSelectDTO == null) {
            return Result.fail("参数不能为空");
        }
        Long pageNo = paperSelectDTO.getPageNo();
        Long pageSize = paperSelectDTO.getPageSize();
        if (pageSize == null || pageNo == null || pageNo < 1 || pageSize < 1) {
            return Result.fail("分页的参数不正确");
        }
        Page<Paper> page = new Page<>(pageNo, pageSize);
        Long subjectId = paperSelectDTO.getSubjectId();

        LambdaQueryWrapper<Paper> wrapper = new LambdaQueryWrapper<Paper>();
        if (subjectId != null) {
            wrapper.eq(Paper::getSubjectId, subjectId);
        }

        Page<Paper> paperPage = paperMapper.selectPage(page, wrapper);

        if (paperPage.getTotal() == 0) {
            return Result.success("没有找到试卷");
        }
        List<Paper> papers = paperPage.getRecords();

        List<PaperDTO> paperDTOS = new ArrayList<>();
        for (Paper paper : papers) {
            PaperDTO paperDTOTemp = new PaperDTO();
            PaperBO paperBO = new PaperBO();
            BeanUtils.copyProperties(paper, paperBO);
            // 查询subjectName
            String subjectName =
                    subjectMapper.selectOne(new LambdaQueryWrapper<Subject>().eq(Subject::getId, paper.getSubjectId())).getName();
            paperBO.setSubjectName(subjectName);

            paperDTOTemp.setPreamble(paper.getPreamble());
            paperDTOTemp.setPaper(paperBO);
            // 查询questionIds
            paperDTOTemp.setQuestionIds(pqMapper.selectList(new LambdaQueryWrapper<Pq>()
                            .eq(Pq::getPaperId, paper.getId()))
                    .stream()
                    .map(Pq::getQuestionId)
                    .collect(Collectors.toList()));
            paperDTOS.add(paperDTOTemp);
        }
        return Result.success(PageUtils.getResMapByPage(paperPage, "papers", paperDTOS));
    }

    @Override
    public Result<?> getPaperById(Long id) {
        if (id == null) {
            return Result.fail("参数不能为空");
        }
        PaperDTO paperDTO = new PaperDTO();
        Paper paper = paperMapper.selectOne(new LambdaQueryWrapper<Paper>().eq(Paper::getId, id));
        if (paper == null) {
            return Result.fail("试卷不存在");
        }

        // 把paper对象转成paperBO对象
        PaperBO paperBO = new PaperBO();
        BeanUtils.copyProperties(paper, paperBO);

        paperDTO.setPaper(paperBO);

        // 查询questionIds
        ArrayList<Long> questionIds = new ArrayList<>();
        List<Pq> pqs = pqMapper.selectList(new LambdaQueryWrapper<Pq>().select(Pq::getQuestionId).eq(Pq::getPaperId, id));
        pqs.forEach(pq -> questionIds.add(pq.getQuestionId()));
        paperDTO.setQuestionIds(questionIds);

        // 考前须知
        paperDTO.setPreamble(paper.getPreamble());
        return Result.success(paperDTO);
    }

    @Override
    public Result<?> getQuestionTotalScore(List<Long> questionIds) {
        if(questionIds == null || questionIds.isEmpty()) {
            return Result.fail("参数不能为空");
        }
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>().select(Question::getQuestionScore).in(Question::getId, questionIds));
        if(questions.isEmpty()){
            return Result.success(0);
        }else {
            return Result.success(questions.stream().map(Question::getQuestionScore).reduce(Integer::sum).get());
        }
    }



}
