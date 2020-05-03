package com.ananops.provider.model.dto;

import com.ananops.provider.model.domain.MdmcTask;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
public class MdmcPageDto implements Serializable {


    private static final long serialVersionUID = -2758697307053426556L;
    @ApiModelProperty("页码")
    private Integer pageNum;

    @ApiModelProperty("记录数")
    private Integer pageSize;

    @ApiModelProperty("工单列表")
    private List<MdmcTask> taskList;
}
