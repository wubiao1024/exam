package com.exam.POJO.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublishExamDTO {
    private Long PaperId;
    private List<Long> studentIds;
    /**
     * 考试开始时间
     * */
    private String startTime;
    /*考试时长 分钟*/
    private Integer duration;
}
