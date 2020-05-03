package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 动态表单数据Dto
 *
 * @author Bingyue Duan
 *
 * @version 1.0
 *
 * @date 2020/4/20 下午3:03
 */
@Data
@ApiModel
public class FormDataDto implements Serializable {

    private static final long serialVersionUID = -5189360285416364171L;

    /**
     * 表单数据项Id
     */
    @ApiModelProperty(value = "表单数据项Id")
    private Long id;

    /**
     * 表单Schema Id
     */
    @ApiModelProperty(value = "表单Schema Id")
    private Long schemaId;

    /**
     * 表单模板 Id
     */
    @ApiModelProperty(value = "表单模板 Id")
    private Long templateId;

    /**
     * 点位名称
     */
    @ApiModelProperty(value = "点位名称")
    private String pointName;

    /**
     * 点位地址
     */
    @ApiModelProperty(value = "点位地址")
    private String pointAddress;

    /**
     * 巡检单位
     */
    @ApiModelProperty(value = "巡检单位")
    private String inspcCompany;

    /**
     * 资产描述列表
     */
    @ApiModelProperty(value = "资产描述列表")
    private List<DeviceDesc> assetList;

    /**
     * 常规巡检详情
     */
    @ApiModelProperty(value = "常规巡检详情")
    private List<InspcDetail> inspcDetailList;

    /**
     * 确认内容
     */
    @ApiModelProperty(value = "确认内容")
    private InspcFeedBack feedback;
}
