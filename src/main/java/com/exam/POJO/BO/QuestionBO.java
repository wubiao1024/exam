package com.exam.POJO.BO;

import com.exam.entity.myEnum.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionBO {
    /**
     * XZ - 选择 , TK-填空， JD-简答，PD-判断
     */
    @Schema(title = "XZ - 选择 , TK-填空， JD-简答，PD-判断", type = "string")
    private QuestionType questionType;
    /**
     * 难度
     */
    private Integer level;

    /**
     *  问题总分
     */
    @Schema(title = "问题总分", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer questionScore;
}
