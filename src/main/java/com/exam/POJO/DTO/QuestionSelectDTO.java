package com.exam.POJO.DTO;

import com.exam.entity.myEnum.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSelectDTO {
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long questionId;
    @Schema( requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long subjectId;
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private QuestionType questionType;
    private Long pageNo;
    private Long pageSize;
}
