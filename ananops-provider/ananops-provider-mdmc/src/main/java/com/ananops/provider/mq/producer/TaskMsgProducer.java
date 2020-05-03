package com.ananops.provider.mq.producer;

import com.alibaba.fastjson.JSON;
import com.ananops.RedisKeyUtil;
import com.ananops.base.constant.AliyunMqTopicConstants;
import com.ananops.base.dto.MqSendMsgDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.base.exception.BusinessException;
import com.ananops.provider.model.domain.MdmcTask;
import com.ananops.provider.model.domain.MqMessageData;
import com.ananops.provider.model.dto.mqdto.MdmcSendMsgDto;
import com.ananops.provider.model.enums.MdmcTaskStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

/**
 * Created by huqiaoqian on 2020/2/24
 */
@Slf4j
@Component
public class TaskMsgProducer {
    public MqMessageData sendTaskStatusMsgMq(MdmcTask mdmcTask){
        Long taskId = mdmcTask.getId();
        Long userId=mdmcTask.getUserId();
        Long facilitatorId = mdmcTask.getFacilitatorId();
        Long principalId = mdmcTask.getPrincipalId();
        Long maintainerId=mdmcTask.getMaintainerId();
        int status = mdmcTask.getStatus();
        String statusMsg = MdmcTaskStatusEnum.getStatusMsg(status);

        String msgBody;
        MdmcSendMsgDto mdmcSendMsgDto=new MdmcSendMsgDto();
        MqSendMsgDto<MdmcSendMsgDto> mqSendMsgDto = new MqSendMsgDto<>();
        if((status==1 || status==6 || status==10 ||status==12)&& userId!=null){
                //发给值机员的消息
                mdmcSendMsgDto.setStatus(status);
                mdmcSendMsgDto.setStatusMsg(statusMsg);
                mdmcSendMsgDto.setTaskId(taskId);
                mdmcSendMsgDto.setUserId(userId);
                mqSendMsgDto.setUserId(userId);
                mqSendMsgDto.setMsgBodyDto(mdmcSendMsgDto);
                msgBody = JSON.toJSONString(mqSendMsgDto);
        }else if((status==2||status==8||status==11||status==13)&&principalId!=null){
                //发给用户负责人的消息
                mdmcSendMsgDto.setStatus(status);
                mdmcSendMsgDto.setStatusMsg(statusMsg);
                mdmcSendMsgDto.setTaskId(taskId);
                mdmcSendMsgDto.setUserId(principalId);
                mqSendMsgDto.setUserId(principalId);
                mqSendMsgDto.setMsgBodyDto(mdmcSendMsgDto);
                msgBody = JSON.toJSONString(mqSendMsgDto);
        }else if(status==3 && facilitatorId!=null){
                //发给服务商业务员的消息
                mdmcSendMsgDto.setStatus(status);
                mdmcSendMsgDto.setStatusMsg(statusMsg);
                mdmcSendMsgDto.setTaskId(taskId);
                mdmcSendMsgDto.setUserId(facilitatorId);
                mqSendMsgDto.setUserId(facilitatorId);
                mqSendMsgDto.setMsgBodyDto(mdmcSendMsgDto);
                msgBody = JSON.toJSONString(mqSendMsgDto);
        }else if((status==5 ||status==9 ||status==16 ||status==17)&&maintainerId!=null ){
                //发给维修工程师的消息
                mdmcSendMsgDto.setStatus(status);
                mdmcSendMsgDto.setStatusMsg(statusMsg);
                mdmcSendMsgDto.setTaskId(taskId);
                mdmcSendMsgDto.setUserId(maintainerId);
                mqSendMsgDto.setUserId(maintainerId);
                mqSendMsgDto.setMsgBodyDto(mdmcSendMsgDto);
                msgBody = JSON.toJSONString(mqSendMsgDto);
        }else {
                //找不到发送消息的对象
                throw new BusinessException(ErrorCodeEnum.MDMC99980006);
        }

        String topic = AliyunMqTopicConstants.MqTopicEnum.MDMC_TOPIC.getTopic();
        String tag = AliyunMqTopicConstants.MqTagEnum.MDMC_TASK_STATUS_CHANGED.getTag();
        String key = RedisKeyUtil.createMqKey(topic, tag, String.valueOf(taskId), msgBody);
        log.info("发送维修工单状态改变消息，mqSendMsgDto={}",mqSendMsgDto);
        return new MqMessageData(msgBody, topic, tag, key);
    }
}
