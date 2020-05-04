package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by rongshuai on 2020/5/3 21:46
 */
@Data
@ApiModel
public class RdcAddDeviceDto implements Serializable {
    private static final long serialVersionUID = 7679753695567177396L;

    @ApiModelProperty("设备唯一ID")
    private Long id;

    @ApiModelProperty("设备名称")
    private String name;

    @ApiModelProperty("设备类型")
    private String type;

    @ApiModelProperty("设备生产商")
    private String manufacture;

    @ApiModelProperty("设备型号")
    private String model;

    @ApiModelProperty("设备在全景图中的经度")
    private BigDecimal longitude;

    @ApiModelProperty("设备在全景图中的纬度")
    private BigDecimal latitude;

    @ApiModelProperty("设备图片的流水号")
    private String refNo;

    @ApiModelProperty("设备ID（与物管理对接要用）")
    private String deviceId;

    @ApiModelProperty("组织ID")
    private Long groupId;

    /**
     * 附件id
     */
    @ApiModelProperty(value = "场景对应的附件id")
    private List<String> attachmentIds;

}
