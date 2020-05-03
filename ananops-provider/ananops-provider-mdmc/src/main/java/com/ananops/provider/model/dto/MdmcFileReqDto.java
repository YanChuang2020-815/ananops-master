package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "MdmcFileReqDto")
public class MdmcFileReqDto implements Serializable {
    private static final long serialVersionUID = 6674466098926304940L;

    /**
     * 工单id
     */
    @ApiModelProperty(value = "工单id")
    private Long TaskId;

    /**
     * 工单状态
     */
    @ApiModelProperty(value = "工单状态")
    private Integer status;

}
