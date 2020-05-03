package com.ananops.provider.model.dto;

import com.ananops.provider.model.dto.oss.ElementImgUrlDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rongshuai on 2020/2/20 16:45
 */
@Data
@ApiModel
public class ImcItemUrlDto implements Serializable {
    private static final long serialVersionUID = 5887722360758928683L;

    @ApiModelProperty(value = "巡检任务id")
    private Long taskId;

    @ApiModelProperty(value = "巡检任务子项id")
    private Long itemId;

    @ApiModelProperty(value = "巡检任务子项状态")
    private int itemStatus;

    @ApiModelProperty(value = "巡检任务子项对应的图片列表")
    private List<ElementImgUrlDto> elementImgUrlDtos;
}
