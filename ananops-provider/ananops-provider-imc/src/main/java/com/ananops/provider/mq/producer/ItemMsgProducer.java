package com.ananops.provider.mq.producer;

import com.alibaba.fastjson.JSON;
import com.ananops.RedisKeyUtil;
import com.ananops.base.constant.AliyunMqTopicConstants;
import com.ananops.base.dto.MqSendMsgDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.base.exception.BusinessException;
import com.ananops.core.mq.MqMessage;
import com.ananops.provider.mapper.ImcInspectionTaskMapper;
import com.ananops.provider.model.domain.ImcInspectionItem;
import com.ananops.provider.model.domain.ImcInspectionTask;
import com.ananops.provider.model.domain.MqMessageData;
import com.ananops.provider.model.dto.mqDto.ImcSendItemStatusDto;
import com.ananops.provider.model.enums.ItemStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by rongshuai on 2020/2/22 16:19
 */
@Slf4j
@Component
public class ItemMsgProducer {

    @Resource
    ImcInspectionTaskMapper imcInspectionTaskMapper;


    public MqMessageData sendItemStatusMsgMq(ImcInspectionItem imcInspectionItem){
        log.info("巡检子项发送状态变更消息：imcInspectionItem={}",imcInspectionItem);
        Long taskId = imcInspectionItem.getInspectionTaskId();
        Long itemId = imcInspectionItem.getId();
        Long maintainerId = imcInspectionItem.getMaintainerId();
        int status = imcInspectionItem.getStatus();
        String statusMsg = ItemStatusEnum.getStatusMsg(status);
        ImcInspectionTask imcInspectionTask = imcInspectionTaskMapper.selectByPrimaryKey(taskId);
        log.info("子项对应的任务：imcInspectionTask={}",imcInspectionTask);
        String msgBody = "";
        ImcSendItemStatusDto imcSendItemStatusDto = new ImcSendItemStatusDto();
        MqSendMsgDto<ImcSendItemStatusDto> mqSendMsgDto = new MqSendMsgDto<>();
        try{
            Long principalId = imcInspectionTask.getPrincipalId();
            Long facilitatorId = imcInspectionTask.getFacilitatorId();
            if(status==2||status==5){
                //给工程师发消息
                imcSendItemStatusDto.setItemId(itemId);
                imcSendItemStatusDto.setStatus(status);
                imcSendItemStatusDto.setStatusMsg(statusMsg);
                imcSendItemStatusDto.setUserId(maintainerId);
                mqSendMsgDto.setUserId(maintainerId);
                mqSendMsgDto.setMsgBodyDto(imcSendItemStatusDto);
            }else if(status==1){
                //给服务商发消息
                imcSendItemStatusDto.setItemId(itemId);
                imcSendItemStatusDto.setStatus(status);
                imcSendItemStatusDto.setStatusMsg(statusMsg);
                imcSendItemStatusDto.setUserId(facilitatorId);
                mqSendMsgDto.setUserId(facilitatorId);
                mqSendMsgDto.setMsgBodyDto(imcSendItemStatusDto);
            }else if(status==3||status==4){
                //给甲方负责人
                imcSendItemStatusDto.setItemId(itemId);
                imcSendItemStatusDto.setStatus(status);
                imcSendItemStatusDto.setStatusMsg(statusMsg);
                imcSendItemStatusDto.setUserId(principalId);
                mqSendMsgDto.setUserId(principalId);
                mqSendMsgDto.setMsgBodyDto(imcSendItemStatusDto);
            }else{
                //无此状态
                throw new BusinessException(ErrorCodeEnum.GL9999094);
            }
        }catch (Exception e){
            throw new BusinessException(ErrorCodeEnum.IMC10090007);
        }
        msgBody = JSON.toJSONString(mqSendMsgDto);
        String topic = AliyunMqTopicConstants.MqTopicEnum.IMC_TOPIC.getTopic();
        String tag = AliyunMqTopicConstants.MqTagEnum.IMC_ITEM_STATUS_CHANGED.getTag();
        String key = RedisKeyUtil.createMqKey(topic, tag, String.valueOf(itemId), msgBody);
        log.info("发送巡检任务子项状态改变消息，mqSendMsgDto={}",mqSendMsgDto);
        return new MqMessageData(msgBody, topic, tag, key);
    }
}
