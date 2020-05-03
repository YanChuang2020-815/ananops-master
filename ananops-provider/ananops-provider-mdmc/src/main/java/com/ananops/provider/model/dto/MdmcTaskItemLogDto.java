package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel
public class MdmcTaskItemLogDto implements Serializable {
    private static final long serialVersionUID = -6872931294973491007L;
    @ApiModelProperty("工单编号")
    private Long taskId;

    @ApiModelProperty("操作描述")
    private String description;
}
