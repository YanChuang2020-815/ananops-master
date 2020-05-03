package com.ananops.provider.model.domain;

import com.ananops.core.mybatis.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import javax.persistence.*;

/**
 * 表单模板项
 *
 * @author Bingyue Duan
 *
 * @version 1.0
 *
 * @date 2020/4/19 下午5:14
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "an_mdc_form_template_item")
public class MdcFormTemplateItem extends BaseEntity {

    private static final long serialVersionUID = -1200527906370131030L;

    /**
     * 模板ID
     */
    @Column(name = "template_id")
    private Long templateId;

    /**
     * 表单类型（device/inspeContent）
     */
    private String type;

    /**
     * 内容描述
     */
    private String content;

    /**
     * 模板单项序号
     */
    private Integer sort;
}