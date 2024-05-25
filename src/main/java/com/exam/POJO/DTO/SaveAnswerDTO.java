package com.exam.POJO.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveAnswerDTO {
    private Long examRecordId;
    private Long questionId;
    private String answer;
}
