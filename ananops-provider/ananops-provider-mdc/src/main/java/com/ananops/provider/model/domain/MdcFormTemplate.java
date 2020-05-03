package com.ananops.provider.model.domain;

import com.ananops.core.mybatis.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import javax.persistence.*;

/**
 * 表单模板
 *
 * @author Bingyue Duan
 *
 * @version 1.0
 *
 * @date 2020/4/19 下午5:14
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "an_mdc_form_template")
public class MdcFormTemplate extends BaseEntity {

    private static final long serialVersionUID = 5882387658365228666L;

    /**
     * Schema ID
     */
    @Column(name = "schema_id")
    private Long schemaId;

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
     * 关联的项目名称
     */
    @Column(name = "project_name")
    private String projectName;

    /**
     * 逻辑删除
     */
    private String dr;
}