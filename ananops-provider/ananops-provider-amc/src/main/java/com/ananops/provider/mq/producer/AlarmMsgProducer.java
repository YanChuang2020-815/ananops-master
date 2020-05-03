package com.ananops.provider.mq.producer;

import com.ananops.RedisKeyUtil;
import com.ananops.base.constant.AliyunMqTopicConstants;
import com.ananops.base.dto.MqSendMsgDto;
import com.ananops.base.exception.BusinessException;
import com.ananops.provider.model.domain.AmcAlarm;
import com.ananops.provider.model.domain.MqMessageData;
import com.ananops.provider.model.dto.AmcSendAlarmOccurDto;
import com.ananops.provider.model.service.UacGroupFeignApi;
import com.ananops.wrapper.Wrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created By ChengHao On 2020/2/29
 */
@Component
@Slf4j
public class AlarmMsgProducer {

    @Resource
    UacGroupFeignApi uacGroupFeignApi;

    /**
     * 警情发生时发送mq消息
     *
     * @param amcAlarm
     * @return
     */
    public MqMessageData sendAlarmOccurMsgMq(AmcAlarm amcAlarm) {
        Wrapper<List<Long>> wrapper = uacGroupFeignApi.getUacUserIdListByGroupId(amcAlarm.getGroupId());
        List<Long> userIdList = wrapper.getResult();

        AmcSendAlarmOccurDto amcSendAlarmOccurDto = new AmcSendAlarmOccurDto();
        amcSendAlarmOccurDto.setAlarmId(amcAlarm.getId());
        amcSendAlarmOccurDto.setAlarmLevel(amcAlarm.getAlarmLevel());
        amcSendAlarmOccurDto.setAlarmLocation(amcAlarm.getAlarmLocation());
        amcSendAlarmOccurDto.setLastOccurTime(amcAlarm.getLastOccurTime());

        if (userIdList.size() > 0) {
            amcSendAlarmOccurDto.setUserId(userIdList.get(0));
        }
        String msgBody;
        MqSendMsgDto<AmcSendAlarmOccurDto> mqSendMsgDto = new MqSendMsgDto<>();
        if (amcSendAlarmOccurDto.getUserId() != null) {
            mqSendMsgDto.setMsgBodyDto(amcSendAlarmOccurDto);
            msgBody = JSON.toJSONString(mqSendMsgDto);
        } else {
            throw new BusinessException("报警信息没有处理人");
        }
        String topic = AliyunMqTopicConstants.MqTopicEnum.AMC_TOPIC.getTopic();
        String tag = AliyunMqTopicConstants.MqTagEnum.AMC_ALARM_OCCUR.getTag();
        String key = RedisKeyUtil.createMqKey(topic, tag, String.valueOf(amcAlarm.getId()), msgBody);
        log.info("发送报警信息，mqSendMsgDto={}", mqSendMsgDto);
        return new MqMessageData(msgBody, topic, tag, key);
    }
}
