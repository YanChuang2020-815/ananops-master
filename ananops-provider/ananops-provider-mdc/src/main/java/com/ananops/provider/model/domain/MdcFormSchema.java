package com.ananops.provider.model.domain;

import com.ananops.core.mybatis.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import javax.persistence.*;

/**
 * 表单结构
 *
 * @author Bingyue Duan
 *
 * @version 1.0
 *
 * @date 2020/4/19 下午5:14
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "an_mdc_form_schema")
public class MdcFormSchema extends BaseEntity {

    private static final long serialVersionUID = 2962332965491053278L;

    /**
     * 表单名称
     */
    private String name;

    /**
     * FormRender的属性结构
     */
    @Column(name = "props_schema")
    private String propsSchema;

    /**
     * FormRender的UI结构
     */
    @Column(name = "ui_schema")
    private String uiSchema;

    /**
     * 表单类型（system/user）
     */
    private String type;

    /**
     * 表单状态
     */
    private String status;

    /**
     * 表单备注说明
     */
    private String mark;

    /**
     * 公司组织ID
     */
    @Column(name = "group_id")
    private Long groupId;

    /**
     * 关联的项目ID
     */
    @Column(name = "project_id")
    private Long projectId;

    /**
     * 逻辑删除
     */
    private String dr;
}