package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 巡检项内容详情
 *
 * @author Bingyue Duan
 *
 * @version 1.0
 *
 * @date 2020/4/20 下午3:18
 */
@Data
@ApiModel
public class InspcDetail implements Serializable {

    private static final long serialVersionUID = -2677421198416899983L;

    /**
     * 巡检内容项Id
     */
    @ApiModelProperty(value = "巡检内容项Id")
    private Long id;

    /**
     * 巡检内容
     */
    @ApiModelProperty(value = "巡检内容")
    private String itemContent;

    /**
     * 本次情况
     */
    @ApiModelProperty(value = "本次情况")
    private String itemState;

    /**
     * 处理结果
     */
    @ApiModelProperty(value = "处理结果")
    private String itemResult;
}
