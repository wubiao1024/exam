package com.exam.POJO.BO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QAutoBO {
    private String A;

    /**
     * 选项B
     */
    private String B;

    /**
     * 选项C
     */
    private String C;

    /**
     * 选项D
     */
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
