package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel
public class MdmcTroubleAddressDto implements Serializable {
    private static final long serialVersionUID = -7409330306626883489L;
    /**
     * 故障位置
     */
    @ApiModelProperty(value = "故障位置")
    private String troubleAddress;

    /**
     * 故障经度
     */
    @ApiModelProperty(value = "故障经度")
    private BigDecimal troubleLongitude;

    /**
     * 故障纬度
     */
    @ApiModelProperty(value = "故障纬度")
    private BigDecimal troubleLatitude;
}
