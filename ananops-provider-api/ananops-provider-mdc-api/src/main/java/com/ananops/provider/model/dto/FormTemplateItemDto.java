package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 表单模板项
 *
 * @author Bingyue Duan
 *
 * @version 1.0
 *
 * @date 2020/4/21 下午4:13
 */
@Data
@ApiModel
public class FormTemplateItemDto implements Serializable {

    private static final long serialVersionUID = -2447204987454370966L;

    /**
     * 表单模板项 Id
     */
    @ApiModelProperty(value = "表单模板项 Id")
    private Long id;

    /**
     * 表单模板项内容
     */
    @ApiModelProperty(value = "表单模板项内容")
    private String content;

    /**
     * 表单模板项序号
     */
    @ApiModelProperty(value = "表单模板项序号")
    private Integer sort;
}
