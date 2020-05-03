package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by huqiaoqian on 2020/3/8
 */
@Data
@ApiModel
public class MdmcUserWatcherDto implements Serializable {
    private static final long serialVersionUID = 1379315759142492929L;
    /**
     * 值机员id
     */
    @ApiModelProperty(value = "值机员id")
    private Long userId;

    /**
     * 值机员姓名
     */
    @ApiModelProperty(value = "值机员姓名")
    private String userName;
}
