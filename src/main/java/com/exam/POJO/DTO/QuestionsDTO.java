package com.exam.POJO.DTO;

import com.exam.POJO.BO.QuestionInfoBO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionsDTO {
    private Integer TotalScore;
    private Integer TotalCount;
    private List<QuestionInfoBO> contentList;
}


