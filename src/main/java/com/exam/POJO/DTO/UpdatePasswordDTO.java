package com.exam.POJO.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdatePasswordDTO {
    private Long id;
    private String oldPassword;
    private String newPassword;
}
