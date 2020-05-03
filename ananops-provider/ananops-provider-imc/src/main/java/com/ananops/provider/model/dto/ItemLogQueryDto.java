package com.ananops.provider.model.dto;

import com.ananops.base.dto.BaseQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by rongshuai on 2019/12/12 11:09
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel
public class ItemLogQueryDto extends BaseQuery {
    private static final long serialVersionUID = 1872522022248901279L;

    /**
     * 巡检任务子项ID
     */
    @ApiModelProperty(value = "巡检任务子项ID")
    private Long itemId;
}
