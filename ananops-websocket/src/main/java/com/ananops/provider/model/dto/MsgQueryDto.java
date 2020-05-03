package com.ananops.provider.model.dto;

import com.ananops.base.dto.BaseQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by rongshuai on 2020/3/12 9:22
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel
public class MsgQueryDto extends BaseQuery {
    private static final long serialVersionUID = 204702781519915566L;

    /**
     * 消息对应的userId
     */
    @ApiModelProperty(value = "消息对应的userId")
    private Long userId;

    /**
     * 消息对应的tag
     */
    @ApiModelProperty(value = "消息对应的tag")
    private String messageTag;

    /**
     * 消息对应的topic
     */
    @ApiModelProperty(value = "消息对应的topic")
    private String messageTopic;

    /**
     * 消息对应的状态
     */
    @ApiModelProperty(value = "消息对应的状态")
    private Integer status;
}
