package com.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.common.Result;
import com.exam.entity.Subject;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 洛克
 * @since 2024-04-21
 */
public interface ISubjectService extends IService<Subject> {

    Result<?> getAllSubject();
}
