package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author 洛克
 * @since 2024-04-21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("exam_record")
public class ExamRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "uid", type = IdType.AUTO)
    @JsonIgnore
    private Long uid;

    /**
     * 考试记录id
     */
    private Long id;

    /**
     * 试卷id.可以根据该字段在pq表中找到该试题对应的题目，在pq_record表可以找到对应的作答记录
     */
    private Long paperId;

    /**
     * 学生id
     */
    private Integer studentId;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 交卷时间
     */
    private LocalDateTime submitTime;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 0 没有删除 1 删除
     */
    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;


}
