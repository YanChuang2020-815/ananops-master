package com.ananops.provider.model.domain;

import com.ananops.core.mybatis.BaseEntity;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 加盟服务商
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "an_bmc_bill")
public class BmcBill extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2853449914001346057L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "transaction_method")
    private String transactionMethod;

    private BigDecimal amount;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name")
    private String userName;

    private Long supplier;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "project_id")
    private Long projectId;

    private Long time;

    @Column(name = "work_order_id")
    private Long workOrderId;

    private String state;

    @Column(name = "device_amount")
    private BigDecimal deviceAmount;

    @Column(name = "service_amount")
    private BigDecimal serviceAmount;

    private Integer version;

    private String creator;

    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "last_operator")
    private String lastOperator;

    @Column(name = "last_operator_id")
    private Long lastOperatorId;

    @Column(name = "update_time")
    private Date updateTime;
}