package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.exam.POJO.DTO.QuestionDTO;
import com.exam.entity.myEnum.QuestionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>
 * 问题
 * </p>
 *
 * @author 洛克
 * @since 2024-04-23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("question")
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "uid", type = IdType.AUTO)
    @JsonIgnore
    private Long uid;

    private Long id;

    private Long creatorId;

    /**
     * XZ - 选择 , TK-填空， JD-简答，PD-判断
     */
    private QuestionType questionType;

    /**
     *  问题总分
     */
    private Integer questionScore;


    /**
     * 学科id
     */
    private Long subjectId;

    /**
     * 难度
     */
    private Integer level;
    /**
     * 是否是主观题
     */

    private Integer isAuto;

    @TableField(exist = false)
    @JsonProperty("qAuto")
    private QAuto qAuto;

    @TableField(exist = false)
    @JsonProperty("qOperation")
    private QOperation qOperation;

    /**
     * 0 没有删除 1 删除
     */
    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;

    public Question(QuestionDTO questionDTO) {
        BeanUtils.copyProperties(questionDTO,this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Question question)) {
            return false;
        }
        return getQuestionType() == question.getQuestionType() && Objects.equals(getSubjectId(), question.getSubjectId()) && Objects.equals(qAuto, question.qAuto) && Objects.equals(qOperation, question.qOperation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getQuestionType(), getSubjectId(), qAuto, qOperation);
    }


}
