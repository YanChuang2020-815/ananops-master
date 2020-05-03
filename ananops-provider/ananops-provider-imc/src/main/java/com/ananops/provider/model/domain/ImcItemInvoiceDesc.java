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
@Table(name = "an_imc_item_invoice_desc")
public class ImcItemInvoiceDesc extends BaseEntity {

    private static final long serialVersionUID = 4242728413039942983L;

    /**
     * 实例ID
     */
    @Column(name = "invoice_id")
    private Long invoiceId;

    /**
     * 巡检内容
     */
    @Column(name = "item_content")
    private String itemContent;

    /**
     * 本次巡检情况
     */
    @Column(name = "item_state")
    private String itemState;

    /**
     * 处理结果
     */
    @Column(name = "item_result")
    private String itemResult;

    /**
     * 对应序号
     */
    private Integer sort;
}