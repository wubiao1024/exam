package com.exam.POJO.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarkQuestionDTO {
    private Long examRecordId;
    private Long questionId;
    /**
     * 得分
     */
    private String score;
    /**
     * 评语
     */
    private String comment;
}