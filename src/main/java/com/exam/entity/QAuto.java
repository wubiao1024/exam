package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.exam.POJO.BO.QAutoBO;
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
 * 非主观题
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
@TableName("q_auto")
public class QAuto implements Serializable {

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
     * 选项A
     */
    @TableField("A")
    private String A;

    /**
     * 选项B
     */
    @TableField("B")
    private String B;

    /**
     * 选项C
     */
    @TableField("C")
    private String C;

    /**
     * 选项D
     */
    @TableField("D")
    private String D;

    /**
     * 答案
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

    public QAuto(QAutoBO qAuto) {
        this.description = qAuto.getDescription();
        this.A = qAuto.getA();
        this.B = qAuto.getB();
        this.C = qAuto.getC();
        this.D = qAuto.getD();
        this.answer = qAuto.getAnswer();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QAuto qAuto)) {
            return false;
        }
        return Objects.equals(getA(), qAuto.getA()) && Objects.equals(getB(), qAuto.getB()) && Objects.equals(getC(), qAuto.getC()) && Objects.equals(getD(), qAuto.getD()) && Objects.equals(getAnswer(), qAuto.getAnswer()) && Objects.equals(getDescription(), qAuto.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getA(), getB(), getC(), getD(), getAnswer(), getDescription());
    }


}
