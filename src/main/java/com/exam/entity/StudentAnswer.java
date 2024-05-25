package com.exam.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 洛克
 * @since 2024-05-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("student_answer")
public class StudentAnswer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "uid", type = IdType.AUTO)
    private Long uid;

    /**
     * id
     */
    private Long id;

    /**
     * 作答人
     */
    private Long studentId;

    /**
     * 对应哪一次考试
     */
    private Long examRecordId;

    /**
     * 问题id
     */
    private Long questionId;

    /**
     * 学生答案
     */
    private String answer;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 评语
     */
    private String comment;

    private Integer isDeleted;


}
