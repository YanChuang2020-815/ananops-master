package com.ananops.provider.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by rongshuai on 2020/5/6 14:24
 */
@Data
@ApiModel
public class RdcArrowVo implements Serializable {
    private static final long serialVersionUID = -3036134177081660952L;

    @ApiModelProperty(name = "id")
    private Long id;

    private BigDecimal longitude;

    private BigDecimal latitude;

    @ApiModelProperty("指向场景唯一ID")
    private Long sceneId;

    @ApiModelProperty("指向场景名称")
    private String sceneName;

    @ApiModelProperty("指向场景图片信息")
    private String url;

}
