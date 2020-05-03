package com.ananops.provider.model.dto;

import com.ananops.provider.model.dto.oss.OptUploadFileReqDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "MdmcUploadFileReqDto")
public class MdmcUploadFileReqDto implements Serializable {
    private static final long serialVersionUID = 8616365791917132025L;

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

    /**
     * OpcUploadFileReqDto
     */
    @ApiModelProperty(value = "附件信息")
    private OptUploadFileReqDto optUploadFileReqDto;

}
