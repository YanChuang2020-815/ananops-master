package com.ananops.provider.model.dto;

import com.ananops.base.dto.BaseQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/2/11 23:27
 */
@Data
@ApiModel
public class ImcPicQueryDto implements Serializable {
    private static final long serialVersionUID = 1930439737068584535L;

    /**
     * 对应的巡检任务的Id
     */
    @ApiModelProperty(value = "巡检任务子项对应的巡检任务的ID")
    private Long taskId;

    /**
     * 对应的巡检任务子项的Id
     */
    @ApiModelProperty(value = "巡检任务子项对应的巡检任务子项的ID")
    private Long itemId;

    /**
     * 巡检任务子项对应的状态
     */
    @ApiModelProperty(value = "巡检任务子项对应的状态")
    private Integer itemStatus;

}
