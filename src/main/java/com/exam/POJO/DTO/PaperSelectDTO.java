package com.exam.POJO.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaperSelectDTO {
    @Schema( requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long subjectId;
    private Long pageNo;
    private Long pageSize;
    // 考试生效 exam_record
    private Integer status;
    private Long classId;
    // 考生的真实姓名
    private String studentName;
}
