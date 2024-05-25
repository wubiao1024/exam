package com.exam.service;

import com.exam.POJO.BO.ClassBO;
import com.exam.POJO.DTO.ClassSelectDTO;
import com.exam.common.Result;
import com.exam.entity.Class;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 班级 服务类
 * </p>
 *
 * @author 洛克
 * @since 2024-04-16
 */
public interface IClassService extends IService<Class> {

    Result<?> getAllClass();

    Result<?> getAllClassWithPage(ClassSelectDTO classSelectDTO);

    Result<?> addClass(ClassBO classBo);

    Result<?> updateClass(ClassBO classBo);

    Result<?> deleteClass(Integer id);
}
