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
 * 班级
 * </p>
 *
 * @author 洛克
 * @since 2024-04-16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("class")
public class Class implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "uid", type = IdType.AUTO)
    @JsonIgnore
    private Long uid;

    private Long id;

    private String className;
    private String description;

    /**
     * 0 没有删除 1 删除
     */
    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;


}
