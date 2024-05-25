package com.exam.POJO.DTO;

import com.exam.POJO.BO.QAutoBO;
import com.exam.POJO.BO.QOperationBO;
import com.exam.POJO.BO.QuestionBO;
import com.exam.entity.myEnum.QuestionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 问题
 * </p>
 *
 * @author 洛克
 * @since 2024-04-23
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO extends QuestionBO {

    /**
     * 问题 id
     * */
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "只有当更新的时候需要")
    private Long id;



    /**
     * 学科中文名.只读
     */
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String subjectName;


    /**
     * 学科英文名.只读
     */
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String subjectEnglishName;


    /**
     * 学科id
     * */
    private Long subjectId;
    /**
     *  问题总分
     */
    private Integer questionScore;

    private QuestionType questionType;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer isAuto;


    /**
     * 构造函数
     * */
    @JsonProperty("qAuto")
    private QAutoBO qAuto;
    @JsonProperty("qOperation")
    private QOperationBO qOperation;

}
