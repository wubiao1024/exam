package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 角色
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
@TableName("role")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "uid", type = IdType.AUTO)
    @JsonIgnore
    private Long uid;

    private Long id;

    /**
     * STUDENT-学生, TEACHER-教师, ADMIN-教务处管理员
     */
    private String roleName;

    private String description;

    /**
     * 0 没有删除 1 删除
     */
    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;


}
