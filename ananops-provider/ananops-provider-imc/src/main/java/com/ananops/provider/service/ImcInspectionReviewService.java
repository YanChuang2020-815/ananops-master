package com.ananops.provider.service;

import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.IService;
import com.ananops.provider.model.domain.ImcInspectionReview;
import com.ananops.provider.model.dto.ImcAddInspectionReviewDto;

/**
 * Created by rongshuai on 2019/12/3 9:11
 */
public interface ImcInspectionReviewService extends IService<ImcInspectionReview> {

    ImcInspectionReview addInspectionReview(ImcInspectionReview imcInspectionReview, LoginAuthDto loginAuthDto);

    ImcInspectionReview getInspectionReviewByTaskId(Long taskId);//根据任务ID，获取当前任务对应的评论

    /**
     * 合并确认完成&评价
     *
     * @param imcAddInspectionReviewDto
     *
     * @param loginAuthDto
     *
     * @return
     */
    ImcInspectionReview confirmRating(ImcAddInspectionReviewDto imcAddInspectionReviewDto, LoginAuthDto loginAuthDto);
}
