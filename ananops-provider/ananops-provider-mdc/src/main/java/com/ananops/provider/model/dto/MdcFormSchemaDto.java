package com.ananops.provider.model.dto;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;

/**
 * 获取自定义表单结构
 *
 * @author Bingyue Duan
 *
 * @version 1.0
 *
 * @date 2020/4/19 下午5:14
 */
@Data
@ApiModel
public class MdcFormSchemaDto implements Serializable {

    private static final long serialVersionUID = -3832354441677634193L;

    /**
     * 表单Schema Id
     */
    @ApiModelProperty(value = "表单Schema Id")
    private Long id;

    /**
     * 表单名称
     */
    @ApiModelProperty(value = "表单名称")
    private String name;

    /**
     * FormRender的属性结构
     */
    @ApiModelProperty(value = "FormRender的属性结构")
    private JSONObject propsSchema;

    /**
     * FormRender的UI结构
     */
    @ApiModelProperty(value = "FormRender的UI结构")
    private JSONObject uiSchema;

    /**
     * 表单类型（system/user）
     */
    @ApiModelProperty(value = "表单类型（system/user）")
    private String type;

    /**
     * 表单状态
     */
    @ApiModelProperty(value = "表单状态")
    private String status;

    /**
     * 表单备注说明
     */
    @ApiModelProperty(value = "表单备注说明")
    private String mark;

    /**
     * 公司组织ID
     */
    @ApiModelProperty(value = "公司组织ID")
    private Long groupId;

    /**
     * 关联的项目ID
     */
    @ApiModelProperty(value = "关联的项目ID")
    private Long projectId;

    /**
     * 逻辑删除
     */
    @ApiModelProperty(value = "逻辑删除")
    private String dr;
}
