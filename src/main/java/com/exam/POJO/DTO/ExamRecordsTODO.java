package com.exam.POJO.DTO;

import com.exam.POJO.BO.PaperBO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamRecordsTODO{
    @Schema(description = "考试id-和exam_records表的id字段对应",accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    @Schema(description = "试卷标题",accessMode = Schema.AccessMode.READ_ONLY)
    private String title;
    @Schema(description = "试卷描述",accessMode = Schema.AccessMode.READ_ONLY)
    private String description;
    @Schema(description = "试卷总分",accessMode = Schema.AccessMode.READ_ONLY)
    private Integer totalScore;
    @Schema(description = "考试科目名称",accessMode = Schema.AccessMode.READ_ONLY)
    private String subjectName;
    @Schema(description = "考试时长(分钟)")
    private Integer duration;
    @Schema(description = "开始时间",accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime startTime;
    @Schema(description = "paperId",accessMode = Schema.AccessMode.READ_ONLY)
    private Long paperId;
}