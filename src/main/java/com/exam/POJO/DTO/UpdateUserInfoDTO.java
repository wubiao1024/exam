package com.exam.POJO.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserInfoDTO {
    /**
     * 用户id
     */
    private Long id;
    /**
     * 登录名
     */
    private String username;


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
   //    private Long classId;

    /**
     * 性别 gender 0 男 1 女
     */
    private Integer gender;

    /**
     *邮箱 email
     */
    private String email;
}
