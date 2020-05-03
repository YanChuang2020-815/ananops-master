package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by huqiaoqian on 2020/3/27
 */
@Data
@ApiModel
public class MdcAddDictDto implements Serializable {
    private static final long serialVersionUID = 6008944267115633917L;

    @ApiModelProperty("字典库ID")
    private Long id;

    @ApiModelProperty("字典库等级：sys代表系统，不可由管理员以外其他角色操作；ser代表业务，可由其他角色更改")
    private String dictLevel;

    @ApiModelProperty("字典库状态：Y：启用；N：禁用")
    private String status;

    @ApiModelProperty("备注")
    private String mark;

    @ApiModelProperty("组织id")
    private Long groupId;


    @ApiModelProperty("字典库名称")
    private String name;
}
