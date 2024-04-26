package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.common.Result;
import com.exam.entity.Subject;
import com.exam.mapper.SubjectMapper;
import com.exam.service.ISubjectService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 洛克
 * @since 2024-04-21
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements ISubjectService {
    @Resource
    SubjectMapper subjectMapper;

    @Override
    public Result<?> getAllSubject() {
        LambdaQueryWrapper<Subject> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .select(Subject::getId, Subject::getName, Subject::getEnglishName, Subject::getDescription);


        List<Map<String, Object>> maps = subjectMapper.selectMaps(wrapper);
        System.out.println(maps);

        return Result.success(maps);
    }
}
