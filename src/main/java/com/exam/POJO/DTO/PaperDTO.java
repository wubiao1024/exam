package com.exam.POJO.DTO;

import com.exam.POJO.BO.PaperBO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaperDTO {
    @Schema(description = "试卷基本信息", name = "paper")
    private PaperBO paper;
    @Schema(description = "试卷题目信息", name = "questionIds", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<Long> questionIds;
    @Schema(description = "考前须知", name = "preamble")
    private String preamble;
}
