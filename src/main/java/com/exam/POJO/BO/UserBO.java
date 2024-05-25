package com.exam.POJO.BO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBO {

    private String username;
    /**
     * 密码
     */
   //    @JsonIgnore
   //    private String password;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 联系方式
     */
    private String contactInfo;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 班级id
     * */
    private Long classId;
    /**
     * 班级名称
     * */
    private String className;

    /**
     * 性别 gender 0 男 1 女
     */
    private Integer gender;

    /**
     *邮箱 email
     */
    private String email;

    /**
     * 用户登录时候的身份 currentRole STUDENT TEACHER ADMIN
     */
    private String currentRole;

}


