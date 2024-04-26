package com.exam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.entity.StudentPq;
import com.exam.mapper.StudentPqMapper;
import com.exam.service.IStudentPqService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 学生-答题表 服务实现类
 * </p>
 *
 * @author 洛克
 * @since 2024-03-05
 */
@Service
public class StudentPqServiceImpl extends ServiceImpl<StudentPqMapper, StudentPq> implements IStudentPqService {

}
