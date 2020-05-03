package com.ananops.provider.model.domain;

import com.ananops.core.mybatis.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "an_user_message_info")
public class WebsocketUserMessageInfo extends BaseEntity {
    private static final long serialVersionUID = -8273297626714161303L;
    /**
     * 消息发送的用户ID
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 消息状态（1已读，0未读）
     */
    private Integer status;

    /**
     * topic
     */
    @Column(name = "message_topic")
    private String messageTopic;

    /**
     * tag
     */
    @Column(name = "message_tag")
    private String messageTag;

    /**
     * 消息内容
     */
    @Column(name = "message_body")
    private String messageBody;


}