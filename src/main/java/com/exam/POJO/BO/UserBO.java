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

}


