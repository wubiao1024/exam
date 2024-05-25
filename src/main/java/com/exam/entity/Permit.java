package com.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 洛克
 * @since 2024-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("permit")
public class Permit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "uid", type = IdType.AUTO)
    private Long uid;

    /**
     * 路由权限的id
     */
    private Long id;

    /**
     * 父级路由，0 代表是一级路由
     */
    private Long pid;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 接口资源
     */
    @TableField("interface")
    private String interfacePath;


}
