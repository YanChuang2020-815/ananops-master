package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/2/24 23:57
 */
@Data
@ApiModel
public class ConsumerSubscribeTopicDto implements Serializable {
    private static final long serialVersionUID = -2255873102499901114L;

    /**
     * 消费者组
     */
    @ApiModelProperty(value = "消费者组")
    private String consumerCode;

    /**
     * 主题编码
     */
    @ApiModelProperty(value = "主题编码")
    private String topicCode;
}
