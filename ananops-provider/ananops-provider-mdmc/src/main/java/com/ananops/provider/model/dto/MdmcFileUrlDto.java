package com.ananops.provider.model.dto;

import com.ananops.provider.model.dto.oss.ElementImgUrlDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by huqiaoqian on 2020/2/27
 */
@Data
@ApiModel
public class MdmcFileUrlDto implements Serializable {
    private static final long serialVersionUID = -7461419133368470869L;

    /**
     * 工单状态
     */
    @ApiModelProperty(value = "工单状态")
    private int status;

    /**
     * 图片URL
     */
    @ApiModelProperty(value = "文件URL列表")
    private List<ElementImgUrlDto> elementImgUrlDtoList;
}
