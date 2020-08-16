package com.ananops.provider.mqConsumer;

/**
 * Created by rongshuai on 2020/5/21 15:19
 */

import com.alibaba.fastjson.JSON;
import com.ananops.provider.model.dto.*;
import com.ananops.provider.service.WebSocketFeignApi;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class MQConsumeMsgListenerProcessor implements MessageListenerConcurrently {
    public static final Logger LOGGER = LoggerFactory.getLogger(MQConsumeMsgListenerProcessor.class);

    @Resource
    MsgProcesser msgProcesser;

    @Resource
    WebSocketFeignApi webSocketFeignApi;

    /**
     * 默认msg里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息
     * 不要抛异常，如果没有return CONSUME_SUCCESS ，consumer会重新消费该消息，直到return CONSUME_SUCCESS
     * @param msgList
     * @param consumeConcurrentlyContext
     * @return
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgList, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        if (CollectionUtils.isEmpty(msgList)) {
            LOGGER.info("MQ接收消息为空，直接返回成功");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        MessageExt messageExt = msgList.get(0);
        LOGGER.info("MQ接收到的消息为：" + messageExt.toString());
        try {
            String topic = messageExt.getTopic();
            String tags = messageExt.getTags();
            String body = new String(messageExt.getBody(), "utf-8");

            //LOGGER.info("MQ消息topic={}, tags={}, 消息内容={}", topic,tags,body);
            if (topic.equals("deviceData")) {
                DeviceDataDto deviceDataDto = JSON.parseObject(body,DeviceDataDto.class);
                LOGGER.info("deviceDataDto={}",deviceDataDto);
                msgProcesser.msgProcess(deviceDataDto);
            } else if (topic.equals("edgeDeviceData")) {
                EdgeCurDataDto edgeCurDataDto = JSON.parseObject(body,EdgeCurDataDto.class);
                LOGGER.info("edgeCurDataDto is {}",edgeCurDataDto);
                EdgeDeviceDataDto edgeDeviceDataDto = new EdgeDeviceDataDto();
                edgeDeviceDataDto.setDeviceName(edgeCurDataDto.getDeviceName());
                edgeDeviceDataDto.setAction("CHECK");
                String userId = "896330256212820992";
                edgeDeviceDataDto.setUserId(Long.valueOf(userId));
                List<DeviceTwin> deviceTwins = new ArrayList<>();
                for (SingleDataDto dataDto : edgeCurDataDto.getDataList()) {
                    DeviceTwin deviceTwin = new DeviceTwin();
                    deviceTwin.setPropertyName(dataDto.getName());
                    DeviceReported deviceReported = new DeviceReported();
                    deviceReported.setValue(dataDto.getValue());
                    deviceTwin.setReported(deviceReported);
                    deviceTwins.add(deviceTwin);
                }
                edgeDeviceDataDto.setDeviceTwins(deviceTwins);
                webSocketFeignApi.pushEdgeDeviceData(edgeDeviceDataDto);
            }

        } catch (Exception e) {
            LOGGER.error("获取MQ消息内容异常{}",e);
        }
        // TODO 处理业务逻辑
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}