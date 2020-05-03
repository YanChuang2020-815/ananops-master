package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel
public class MdmcListDto implements Serializable {

    private static final long serialVersionUID = 390810165348427102L;
    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("工单列表")
    private List<MdmcTaskListDto> taskList;

}
