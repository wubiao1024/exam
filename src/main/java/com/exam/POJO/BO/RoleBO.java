package com.exam.POJO.BO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleBO {
    /**
     * STUDENT-学生, TEACHER-教师, ADMIN-教务处管理员
     */
    private String roleName;
    private Long id;

    private String description;
}
