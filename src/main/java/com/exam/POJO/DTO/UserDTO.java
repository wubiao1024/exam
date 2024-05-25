package com.exam.POJO.DTO;

import com.exam.POJO.BO.RoleBO;
import com.exam.POJO.BO.UserBO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO extends UserBO {
     private Long id;
     @Schema(description = "用户具有的身份id列表")
     private List<Long> roleIds;
     @Schema(description = "用户具有身份名称列表")
     private List<RoleBO> roleNames;
}

