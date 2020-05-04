package com.ananops.provider.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by rongshuai on 2020/5/4 7:19
 */
@Data
@ApiModel
public class RdcDeviceVo implements Serializable {
    private static final long serialVersionUID = -1543859026226866323L;

    @ApiModelProperty(name = "id")
    private Long id;

    @ApiModelProperty(name = "设备名称")
    private String name;

    @ApiModelProperty(name = "设备类型")
    private String type;

    @ApiModelProperty(name = "设备生产商")
    private String manufacture;

    @ApiModelProperty(name = "设备型号")
    private String model;

    @ApiModelProperty(name = "设备在全景图中的经度")
    private BigDecimal longitude;

    @ApiModelProperty(name = "设备在全景图中的纬度")
    private BigDecimal latitude;

    @ApiModelProperty(name = "deviceId")
    private String deviceId;

    @ApiModelProperty(name = "场景图片信息")
    private String url;

    @ApiModelProperty(name = "组织id")
    private Long groupId;
}
