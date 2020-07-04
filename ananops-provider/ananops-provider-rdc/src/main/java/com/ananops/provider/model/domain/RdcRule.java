package com.ananops.provider.model.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by rongshuai on 2020/7/4 20:36
 */
@Data
@Table(name = "an_rdc_rule")
public class RdcRule implements Serializable {
    private static final long serialVersionUID = -1369416175969496437L;

    @Column(name = "device_a_id")
    private Long deviceAId;

    @Column(name = "device_b_id")
    private Long deviceBId;

    @Column(name = "trigger_value")
    private Long triggerValue;
}
