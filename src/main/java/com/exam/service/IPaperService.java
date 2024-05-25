package com.exam.service;

import com.exam.POJO.DTO.PaperDTO;
import com.exam.POJO.DTO.PaperSelectDTO;
import com.exam.common.Result;
import com.exam.entity.Paper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 试卷 服务类
 * </p>
 *
 * @author 洛克
 * @since 2024-04-21
 */
public interface IPaperService extends IService<Paper> {

    Result<?> addPaper(PaperDTO paperDTO, String token);

    // 删除试卷
    Result<?> deletePaper(Long id);

    // 修改试卷
    Result<?> updatePaper(PaperDTO paperDTO, String token);


    // 分页查询试卷
    Result<?> findPapers(PaperSelectDTO paperSelectDTO);

    Result<?> getPaperById(Long id);

    Result<?> getQuestionTotalScore(List<Long> questionIds);


}
