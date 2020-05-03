package com.ananops.provider.model.domain;

import com.ananops.core.mybatis.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * 巡检记录表
 *
 * @author Bingyue Duan
 *
 * @version 1.0
 *
 * @date 2020/4/19 下午5:14
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "an_imc_item_invoice")
public class ImcItemInvoice extends BaseEntity {

    private static final long serialVersionUID = 5128701582925429134L;

    /**
     * 模板ID
     */
    @Column(name = "template_id")
    private Long templateId;

    /**
     * 巡检子项ID
     */
    @Column(name = "inspc_item_id")
    private Long inspcItemId;

    /**
     * 点位名称
     */
    @Column(name = "point_name")
    private String pointName;

    /**
     * 点位地址
     */
    @Column(name = "point_address")
    private String pointAddress;

    /**
     * 巡检单位ID
     */
    @Column(name = "inspc_company_id")
    private Long inspcCompanyId;

    /**
     * 巡检单位名称
     */
    @Column(name = "inspc_company")
    private String inspcCompany;

    /**
     * 巡检结论
     */
    @Column(name = "inspc_result")
    private String inspcResult;

    /**
     * 巡检日期
     */
    @Column(name = "inspc_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date inspcDate;

    /**
     * 用户确认
     */
    @Column(name = "user_confirm")
    private String userConfirm;

    /**
     * 巡检工程师ID
     */
    @Column(name = "engineer_id")
    private Long engineerId;

    /**
     * 巡检工程师名称
     */
    private String engineer;

    /**
     * 单据状态
     */
    private String status;

    /**
     * 逻辑删除
     */
    private String dr;
}