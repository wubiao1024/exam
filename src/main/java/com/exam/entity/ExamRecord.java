package com.exam.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
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
 * @since 2024-05-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("exam_record")
public class ExamRecord implements Serializable {

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
     * 试卷id.可以根据该字段在pq表中找到该试题对应的题目，在pq_record表可以找到对应的作答记录
     */
    private Long paperId;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 得分
     */
    private Integer score;

    private Integer isDeleted;

    /**
     * 考试时长
     */
    private Integer duration;

    /**
     * 考试状态，0-没有参加考试, 1-待考试，2-考试中，3-批阅中，4-批阅已完成
     */
    private Integer status;

    /**
     * 进入考试的时间
     */
    private LocalDateTime enterTime;

    /**
     * 是否已经阅读过考前须知
     */
    private Integer isRead;

    /**
     * 正确率
     */
    private Double correctRate;


}
