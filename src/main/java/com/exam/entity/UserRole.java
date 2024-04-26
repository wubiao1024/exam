package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户角色关联表
 * </p>
 *
 * @author 洛克
 * @since 2024-04-25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_role")
public class UserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "uid", type = IdType.AUTO)
    @JsonIgnore
    private Long uid;

    private Long id;

    private Long userId;

    private Long roleId;

    private String description;

    private LocalDateTime updateTime;

    /**
     * 0 没有删除 1 删除
     */
    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;


}
