package com.ananops.provider.consumer.listener;

import com.ananops.PublicUtil;
import com.ananops.core.mq.MqMessage;
import com.ananops.provider.annotation.MqConsumerStore;
import com.ananops.provider.consumer.TopicConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by rongshuai on 2020/2/19 17:31
 */
@Slf4j
@Component
public class WebSocketPushMessageListener implements MessageListenerConcurrently {


    @Resource
    TopicConsumer topicConsumer;

    @Resource
    private StringRedisTemplate srt;

    @Override
    @MqConsumerStore
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messageExtList, ConsumeConcurrentlyContext consumeConcurrentlyContext){
        MessageExt msg = messageExtList.get(0);
        String body = new String(msg.getBody());
        String topicName = msg.getTopic();
        String tags = msg.getTags();
        String keys = msg.getKeys();
        log.info("MQ消费Topic={},tag={},key={}", topicName, tags, keys);
        ValueOperations<String, String> ops = srt.opsForValue();
        // 控制幂等性使用的key
        try {
            MqMessage.checkMessage(body, topicName, tags, keys);
            String mqKV = null;
            if (srt.hasKey(keys)) {
                mqKV = ops.get(keys);
            }
            if (PublicUtil.isNotEmpty(mqKV)) {
                log.error("MQ消费Topic={},tag={},key={}, 重复消费", topicName, tags, keys);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            //处理消息
            topicConsumer.handlerSendMqMsg(body,topicName,tags,keys);
        } catch (IllegalArgumentException ex) {
            log.error("校验MQ message 失败 ex={}", ex.getMessage(), ex);
        } catch (Exception e) {
            log.error("处理MQ message 失败 topicName={}, keys={}, ex={}", topicName, keys, e.getMessage(), e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        ops.set(keys, keys, 10, TimeUnit.DAYS);
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
