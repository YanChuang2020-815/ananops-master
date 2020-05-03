package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BillCreateDto implements Serializable {
    private static final long serialVersionUID = -253316350177648993L;
    @ApiModelProperty(value = "工单ID")
    private Long workOrderId;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "服务商ID")
    private Long supplier;

    @ApiModelProperty(value = "项目ID")
    private Long projectId;

    @ApiModelProperty(value = "支付方式") //填固定字段"未确定"
    private String paymentMethod;

    @ApiModelProperty(value = "状态") //填固定字段未支付
    private String state;

    @ApiModelProperty(value = "备品备件id和数量的列表")
    List<DeviceDto> deviceDtos;

}
