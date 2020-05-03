package com.ananops.provider.model.dto;

import com.ananops.base.dto.BaseQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by rongshuai on 2019/11/28 20:09
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel
public class TaskLogQueryDto extends BaseQuery {
    private static final long serialVersionUID = 8218486271114940287L;
    /**
     * 巡检任务Id
     */
    @ApiModelProperty(value = "巡检任务ID")
    private Long taskId;
}
