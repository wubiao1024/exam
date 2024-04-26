package com.exam.POJO.BO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectBO {
    /**
     * 学科中文名
     */
    private String name;

    /**
     * 学科英文名
     */
    private String englishName;

    /**
     * 描述
     */
    private String description;
}
