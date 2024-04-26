package com.exam.service.impl;

import com.exam.entity.Class;
import com.exam.mapper.ClassMapper;
import com.exam.service.IClassService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 班级 服务实现类
 * </p>
 *
 * @author 洛克
 * @since 2024-04-16
 */
@Service
public class ClassServiceImpl extends ServiceImpl<ClassMapper, Class> implements IClassService {

}
