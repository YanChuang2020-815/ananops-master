package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel
public class MdmcAddTroubleInfoDto implements Serializable {

    private static final long serialVersionUID = -3496178962314962666L;
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    /**
     * 故障类型列表
     */
    @ApiModelProperty(value = "故障类型列表")
    private List<String> troubleTypeList;

    /**
     * 故障位置列表
     */
    @ApiModelProperty(value = "故障位置列表")
    private List<MdmcTroubleAddressDto> troubleAddressList;


}
