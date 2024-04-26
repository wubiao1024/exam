package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.exam.POJO.BO.QOperationBO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>
 * 主观题
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
@TableName("q_operation")
public class QOperation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "uid", type = IdType.AUTO)
    @JsonIgnore
    private Long uid;

    /**
     * id
     */
    private Long id;

    /**
     * 题目id
     */
    private Long qId;

    /**
     * 参考答案
     */
    private String answer;

    /**
     * 题目描述
     */
    private String description;

    /**
     * 0 没有删除 1 删除
     */
    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;

    public QOperation(QOperationBO qOperationBO) {
        this.answer = qOperationBO.getAnswer();
        this.description = qOperationBO.getDescription();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QOperation that)) {
            return false;
        }
        return Objects.equals(getAnswer(), that.getAnswer()) && Objects.equals(getDescription(), that.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAnswer(), getDescription());
    }
}
