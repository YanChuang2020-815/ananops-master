package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Bridge Wang
 * @version 1.0
 * @date 2020/4/11 15:13
 */

@EqualsAndHashCode()
@Data
public class BillStatistics{

    @ApiModelProperty(value = "账单总数")
    private int billNum;
    @ApiModelProperty(value = "某某金额以上的账单总数")
    private int billNumByUserIdAndAmount;
    @ApiModelProperty(value = "创建中状态的账单总数")
    private int billNumByUserIdAndStateCreating;
    @ApiModelProperty(value = "已完成的账单总数")
    private int billNumByUserIdAndStateFinished;
    @ApiModelProperty(value = "现结账单总数")
    private int billNumByUserIdAndStateNowPay;
    @ApiModelProperty(value = "账期账单总数")
    private int billNumByUserIdAndStatePeriodPay;
    @ApiModelProperty(value = "年结账单总数")
    private int billNumByUserIdAndStateYearPay;
    @ApiModelProperty(value = "连续几个月份的账单总金额统计")
    private Object moneySum;
}
