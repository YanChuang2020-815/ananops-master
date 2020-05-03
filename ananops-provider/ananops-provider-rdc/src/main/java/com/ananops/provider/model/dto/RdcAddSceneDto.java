package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rongshuai on 2020/4/27 13:43
 */
@Data
@ApiModel
public class RdcAddSceneDto implements Serializable {
    private static final long serialVersionUID = 8787562469974428560L;

    @ApiModelProperty("场景唯一ID")
    private Long id;

    @ApiModelProperty("场景名称")
    private String sceneName;

    /**
     * 附件id
     */
    @ApiModelProperty(value = "场景对应的附件id")
    private List<String> attachmentIds;
}
