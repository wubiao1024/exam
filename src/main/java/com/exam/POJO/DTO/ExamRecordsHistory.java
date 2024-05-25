package com.exam.POJO.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamRecordsHistory extends ExamRecordsTODO{
    @Schema(description = "考试的状态 1:未开始 2:考试中 3:批阅中 4:批阅已经完成 5-成绩已经发布")
    private Integer status;
    @Schema(description = "得分")
    private Integer score;
    @Schema(description = "考生姓名")
    private String studentName;
    @Schema(description = "班级名称")
    private String className;
    @Schema(description ="正确率")
    private Double correctRate;
}