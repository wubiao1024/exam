package com.exam.POJO.BO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QOperationBO {
    /**
     * answer 参考答案
    * */
    private String answer;
    /**
     * description 问题描述
     * */
    private String description;
}
