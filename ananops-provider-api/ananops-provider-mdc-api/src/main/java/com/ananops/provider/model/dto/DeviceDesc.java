package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 接收资产设备描述内容
 *
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020/4/20 下午3:16
 */
@Data
@ApiModel
public class DeviceDesc implements Serializable {

    private static final long serialVersionUID = 2460857371658814707L;

    /**
     * 设备描述项Id
     */
    @ApiModelProperty(value = "设备描述项Id")
    private Long id;

    /**
     * 设备描述项
     */
    @ApiModelProperty(value = "设备描述项")
    private String device;
}
