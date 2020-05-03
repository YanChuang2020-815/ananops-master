package com.ananops.provider.model.dto;

import com.ananops.provider.model.domain.MdcSysDict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by huqiaoqian on 2020/3/27
 */
@Data
@ApiModel
public class MdcGetDictDto implements Serializable {
    private static final long serialVersionUID = -27217503430504529L;
    @ApiModelProperty("字典库信息")
    private MdcSysDict dict;
}
