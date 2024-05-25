package com.exam.POJO.BO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassBO {
    private Long id;

    private String className;

    private String description;
}
