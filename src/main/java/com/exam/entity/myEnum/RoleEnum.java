package com.exam.entity.myEnum;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum RoleEnum {
    STUDENT(3,"学生"),
    TEACHER(2,"教师"),
    ADMIN(1,"教务处管理员");
    RoleEnum(Integer role, String des) {
        this.role = role;
        this.des = des;
    }
    /*对应数据库的role字段*/
    @EnumValue
    private final Integer role;
    private final String des;


}