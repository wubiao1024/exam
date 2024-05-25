package com.exam.POJO.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassSelectDTO {
    @Schema( requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;
    private String className;
    private Long current;
    private Long pageSize;
}
