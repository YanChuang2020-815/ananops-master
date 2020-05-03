package com.ananops.provider.model.domain;

import com.ananops.core.mybatis.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * Created by huqiaoqian on 2020/3/27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "an_mdc_sys_dict")
public class MdcSysDict extends BaseEntity {
    private static final long serialVersionUID = -5041275675556841474L;
    @Column(name = "dict_level")
    private String dictLevel;

    /**
     * 状态
     */
    private String status;

    private String mark;

    /**
     * 如果为-1，表示都能查看
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


    private String name;

}
