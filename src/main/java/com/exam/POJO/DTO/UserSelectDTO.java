package com.exam.POJO.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSelectDTO {
    private Long current;
    private Long pageSize;
    @Schema(description = "用户id")
    private Long id;
    private Long classId;
    private String username;
    private String realName;

    @Schema(description = "用户角色")
    private String roleId; // getUsers 使用

    private String role; // ADMIN, STUDENT, TEACHER, getUsersWithRole 使用
}
