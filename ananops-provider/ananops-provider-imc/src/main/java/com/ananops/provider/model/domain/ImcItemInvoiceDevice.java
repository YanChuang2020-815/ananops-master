package com.ananops.provider.model.domain;

import com.ananops.core.mybatis.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 表单实例设备统计
 *
 * @author Bingyue Duan
 *
 * @version 1.0
 *
 * @date 2020/4/19 下午5:14
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "an_imc_item_invoice_device")
public class ImcItemInvoiceDevice extends BaseEntity {

    private static final long serialVersionUID = -2163874264272309921L;

    /**
     * 实例ID
     */
    @Column(name = "invoice_id")
    private Long invoiceId;

    /**
     * 设备状态描述
     */
    private String device;

    /**
     * 对应序号
     */
    private Integer sort;
}