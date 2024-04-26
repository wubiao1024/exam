package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 问题 Mapper 接口
 * </p>
 *
 * @author 洛克
 * @since 2024-04-23
 */
@Mapper
@Component
public interface QuestionMapper extends BaseMapper<Question> {

}
