package com.ananops.provider.model.domain;

import lombok.Data;

import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Alias("tpcMqSubscribe")
@Table(name = "an_tpc_mq_subscribe")
@NoArgsConstructor
public class TpcMqSubscribe implements Serializable {
    private static final long serialVersionUID = -7944350792647991631L;
    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 消费者ID
     */
    @Column(name = "consumer_id")
    private Long consumerId;

    /**
     * 消费者组
     */
    @Column(name = "consumer_code")
    private String consumerCode;

    /**
     * TOPIC_ID
     */
    @Column(name = "topic_id")
    private Long topicId;

    /**
     * 主题编码
     */
    @Column(name = "topic_code")
    private String topicCode;

}