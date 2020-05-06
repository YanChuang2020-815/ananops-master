package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by rongshuai on 2020/5/6 14:59
 */
@Data
public class RdcArrowQueryDto implements Serializable {
    private static final long serialVersionUID = -4256595212957843153L;

    @ApiModelProperty(name = "id")
    private Long id;

    private BigDecimal longitude;

    private BigDecimal latitude;

    @ApiModelProperty("指向场景唯一ID")
    private Long sceneId;

    @ApiModelProperty("指向场景名称")
    private String sceneName;

    private String refNo;
}
