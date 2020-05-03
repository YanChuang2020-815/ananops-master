package com.ananops.provider.model.dto;

import com.ananops.provider.model.dto.oss.OptUploadFileReqDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created By ChengHao On 2020/2/13
 */
@Data
@ApiModel(value = "PmcUploadContractReqDto")
public class PmcUploadContractReqDto implements Serializable {
    private static final long serialVersionUID = -1009897798005094602L;

    /**
     * 合同 id
     */
    @ApiModelProperty(value = "contractId")
    private Long contractId;

    /**
     * 文件上传Dto
     */
    @ApiModelProperty(value = "OptUploadFileReqDto")
    private OptUploadFileReqDto optUploadFileReqDto;
}
