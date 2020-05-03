package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020-04-07 16:26
 */
@Data
@ApiModel
public class DictItemDto implements Serializable {

    private static final long serialVersionUID = 8506444370538624462L;

    @ApiModelProperty("字典项id")
    private Long id;

    @ApiModelProperty("从属的字典库id")
    private Long dictId;

    @ApiModelProperty("排序号")
    private Long sort;

    @ApiModelProperty("编码号")
    private String code;

    @ApiModelProperty("公司id")
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
