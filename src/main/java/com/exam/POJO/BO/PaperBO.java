package com.exam.POJO.BO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaperBO {
    private Long id;
    private String title;
    private String description;
    @Schema(description = "试卷总分",accessMode = Schema.AccessMode.READ_ONLY)
    private Integer totalScore;
    private Long subjectId;
    private String subjectName;
    private Integer examDuration;
}
