package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Transient;

/**
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020-03-25 14:50
 */
@Data
@ApiModel(value = "工程师查询Dto")
public class EngineerQueryDto {

    /**
     * 职务
     */
    @Transient
    private String position;

    /**
     * 分页参数
     */
    @Transient
    private Integer pageNum;

    @Transient
    private Integer pageSize;

    @Transient
    private String orderBy;
}
