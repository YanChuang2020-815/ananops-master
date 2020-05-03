package com.ananops.provider.model.domain;

import com.ananops.core.mybatis.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by huqiaoqian on 2020/3/27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "an_mdc_sys_dict_item")
public class MdcSysDictItem  extends BaseEntity {
    private static final long serialVersionUID = -1724453151249859670L;
    @Column(name = "dict_id")
    private Long dictId;

    private Long sort;

    private String code;

    /**
     * 公司组织Id
     */
    @Column(name = "group_id")
    private Long groupId;

    /**
     * 逻辑删除
     */
    private String dr;


    @Column(name = "create_by")
    private String createBy;


    @Column(name = "update_by")
    private String updateBy;

    private BigDecimal latitude;

    private BigDecimal longitude;



    private String name;

    private String mark;
}
