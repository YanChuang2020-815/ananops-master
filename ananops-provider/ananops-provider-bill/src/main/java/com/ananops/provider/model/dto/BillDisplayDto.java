package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Comparator;

@EqualsAndHashCode()
@Data
public class BillDisplayDto {

    
    @ApiModelProperty(value = "账单ID")
    private Long id;

    @ApiModelProperty(value = "支付方式")
    private String paymentMethod;

    @ApiModelProperty(value = "交易方式")
    private String transactionMethod;

    @ApiModelProperty(value = "数额")
    private BigDecimal amount;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户姓名")
    private String userName;

    @ApiModelProperty(value = "当前时间")
    private Long time;

    @ApiModelProperty(value = "服务商id")
    private Long supplier;

    @ApiModelProperty(value = "服务商名称")
    private String supplierName;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "工单ID")
    private Long workOrderId;

    @ApiModelProperty(value = "状态")
    private String state;

    @ApiModelProperty(value = "设备总价")
    private BigDecimal deviceAmount = BigDecimal.valueOf(-1);

    @ApiModelProperty(value = "服务总价")
    private BigDecimal serviceAmount = BigDecimal.valueOf(-1);

    public static class Comparators {
        //根据时间进行排序
        public static Comparator<BillDisplayDto> TIME = new Comparator<BillDisplayDto>() {
            @Override
            public int compare(BillDisplayDto o1, BillDisplayDto o2) {
                return o2.time.compareTo(o1.time);
            }
        };
    }
}
