package com.exam.POJO.BO;

import com.exam.entity.myEnum.QuestionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class QuestionInfoBO extends QuestionBO {
    Long questionId;
    @JsonProperty("A")
    private String A;
    @JsonProperty("B")
    private String B;
    @JsonProperty("C")
    private String C;
    @JsonProperty("D")
    private String D;
    private String description;
    private String correctAnswer;
    private String answer;
    @Schema(description = "教师评语")
    private String comment;
    @Schema(description="得分")
    private Integer score;

    public QuestionInfoBO(QuestionType questionType, Integer level, Integer questionScore, String a, String b, String c, String d, String answer) {
        super(questionType, level, questionScore);
        A = a;
        B = b;
        C = c;
        D = d;
        this.answer = answer;
    }
    public QuestionInfoBO(String a, String b, String c, String d, String answer,String description) {
        A = a;
        B = b;
        C = c;
        D = d;
        this.answer = answer;
        this.description = description;
    }
}
