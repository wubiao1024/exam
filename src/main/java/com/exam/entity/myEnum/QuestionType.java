package com.exam.entity.myEnum;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;


@Getter
public enum QuestionType {
    XZ("XZ", "选择"),
    PD("PD", "判断"),
    TK("TK", "填空"),
    JD("JD", "简答");


    @EnumValue
    private final String questionType;
    private final String description;

    QuestionType(String questionType, String description) {
        this.questionType = questionType;
        this.description = description;
    }
}
