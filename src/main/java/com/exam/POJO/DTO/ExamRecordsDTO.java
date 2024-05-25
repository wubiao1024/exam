package com.exam.POJO.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamRecordsDTO {
    @Schema(description="待考试")
    List<ExamRecordsTODO> examRecordsTODOList;
    @Schema(description="历史考试")
    List<ExamRecordsHistory> examRecordsHistory;
}


