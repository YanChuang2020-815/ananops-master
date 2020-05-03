package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 工程师获取到的表单模板
 *
 * @author Bingyue Duan
 *
 * @version 1.0
 *
 * @date 2020/4/21 下午3:44
 */
@Data
@ApiModel
public class FormTemplateDto implements Serializable {

    private static final long serialVersionUID = 1448803968434776454L;

    /**
     * 表单模板 Id
     */
    @ApiModelProperty(value = "表单模板 Id")
    private Long id;

    /**
     * 表单Schema Id
     */
    @ApiModelProperty(value = "表单Schema Id")
    private Long schemaId;

    /**
     * 资产描述列表
     */
    @ApiModelProperty(value = "资产描述列表")
    private List<FormTemplateItemDto> assetList;

    /**
     * 常规巡检详情
     */
    @ApiModelProperty(value = "常规巡检详情")
    private List<FormTemplateItemDto> inspcDetailList;
}
