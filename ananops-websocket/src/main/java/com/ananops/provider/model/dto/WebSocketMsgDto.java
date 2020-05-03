package com.ananops.provider.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/2/21 15:31
 */
@Data
public class WebSocketMsgDto<T> implements Serializable {
    private static final long serialVersionUID = -6638463898812891463L;
    /**
     * 消息Id
     */
    private Long messageId;
    /**
     * 消息的主题
     */
    private String topic;
    /**
     * 消息的标签
     */
    private String tag;
    /**
     * 消息的内容
     */
    private T content;
}
