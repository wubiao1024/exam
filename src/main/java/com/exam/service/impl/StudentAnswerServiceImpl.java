package com.exam.service.impl;

import com.exam.entity.StudentAnswer;
import com.exam.mapper.StudentAnswerMapper;
import com.exam.service.IStudentAnswerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
