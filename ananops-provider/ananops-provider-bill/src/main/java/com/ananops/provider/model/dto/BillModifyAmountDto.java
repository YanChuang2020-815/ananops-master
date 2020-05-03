package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel
public class BillModifyAmountDto implements Serializable {
    private static final long serialVersionUID = 2542090063167460622L;
    @ApiModelProperty(value = "账单id")
    private Long id;

    @ApiModelProperty(value = "修改金额")
    private BigDecimal modifyAmount;
}
