package com.ananops.provider.model.dto;

import com.ananops.provider.model.domain.MdmcTask;
import com.ananops.provider.model.dto.user.UserInfoDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel
public class MdmcTaskListDto implements Serializable {
    private static final long serialVersionUID = -2161110542642870049L;
    /**
     * 工单内容
     */
    @ApiModelProperty(value = "工单原本内容（没动）")
    private MdmcTask mdmcTask;

    /**
     * 项目信息
     */
    @ApiModelProperty(value = "项目信息")
    private PmcProjectDto pmcProjectDto;


    /**
     * 报修人信息
     */
    @ApiModelProperty(value = "报修人信息")
     private UserInfoDto userInfoDto;


}
