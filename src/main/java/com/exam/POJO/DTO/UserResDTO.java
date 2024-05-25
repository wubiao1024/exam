package com.exam.POJO.DTO;

import com.exam.POJO.BO.RoleBO;
import com.exam.POJO.BO.UserBO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserResDTO extends UserBO {
     private Long id;
     @Schema(description = "用户具有的角色")
     private List<RoleBO> roles;
}

