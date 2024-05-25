package com.exam.POJO.BO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QAutoBO {
    @JsonProperty("A")
    @Schema(name = "A")
    private String A;

    /**
     * 选项B
     */
    @JsonProperty("B")
    @Schema(name = "B")
    private String B;

    /**
     * 选项C
     */
   @JsonProperty("C")
   @Schema(name = "C")
    private String C;

    /**
     * 选项D
     */
    @JsonProperty("D")
    @Schema(name = "D")
    private String D;

    /**
     * 答案
     */
    private String answer;
    /**
     * 题目描述
     */
    private String description;
}
