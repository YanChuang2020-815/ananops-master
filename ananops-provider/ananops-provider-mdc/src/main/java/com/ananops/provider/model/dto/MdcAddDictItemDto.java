package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by huqiaoqian on 2020/3/27
 */
@Data
@ApiModel
public class MdcAddDictItemDto implements Serializable {
    private static final long serialVersionUID = 7949222645428789509L;
    @ApiModelProperty("字典项id")
    private Long id;

    @ApiModelProperty("从属的字典库id")
    private Long dictId;

    @ApiModelProperty("排序号")
    private Long sort;

    @ApiModelProperty("编码号")
    private String code;

    @ApiModelProperty("组织id")
    private Long groupId;

    @ApiModelProperty("经度")
    private BigDecimal latitude;

    @ApiModelProperty("纬度")
    private BigDecimal longitude;

    @ApiModelProperty("字典项名称")
    private String name;

    @ApiModelProperty("备注")
    private String mark;
}
