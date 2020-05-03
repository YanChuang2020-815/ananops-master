package com.ananops.provider.mq.producer;

import com.alibaba.fastjson.JSON;
import com.ananops.RedisKeyUtil;
import com.ananops.base.constant.AliyunMqTopicConstants;
import com.ananops.base.dto.MqSendMsgDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.base.exception.BusinessException;
import com.ananops.provider.mapper.ImcInspectionTaskMapper;
import com.ananops.provider.mapper.ImcUserTaskMapper;
import com.ananops.provider.model.domain.ImcInspectionTask;
import com.ananops.provider.model.domain.ImcUserTask;
import com.ananops.provider.model.domain.MqMessageData;
import com.ananops.provider.model.dto.UndistributedImcTaskDto;
import com.ananops.provider.model.dto.mqDto.ImcSendTaskStatusDto;
import com.ananops.provider.model.enums.TaskStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by rongshuai on 2020/2/22 16:19
 */
@Slf4j
@Component
public class TaskMsgProducer {

    @Resource
    ImcInspectionTaskMapper imcInspectionTaskMapper;

    @Resource
    ImcUserTaskMapper imcUserTaskMapper;

    public MqMessageData sendTaskStatusMsgMq(ImcInspectionTask imcInspectionTask){
        Long taskId = imcInspectionTask.getId();
        Long facilitatorId = imcInspectionTask.getFacilitatorId();
        int status = imcInspectionTask.getStatus();
        String statusMsg = TaskStatusEnum.getStatusMsg(status);
        Example example = new Example(ImcUserTask.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("taskId",taskId);
        List<ImcUserTask> imcUserTaskList = imcUserTaskMapper.selectByExample(example);
        Long userId = null;
        if(imcUserTaskList.size()>0){
            userId = imcUserTaskList.get(0).getUserId();
        }
        String msgBody;
        ImcSendTaskStatusDto imcSendTaskStatusDto = new ImcSendTaskStatusDto();
        MqSendMsgDto<ImcSendTaskStatusDto> mqSendMsgDto = new MqSendMsgDto<>();
        if(userId!=null){
            if(status == -1 ||status==0||status==1||status==3||status==4||status==5||status==6||status==7){
                //发给甲方负责人的消息
                imcSendTaskStatusDto.setStatus(status);
                imcSendTaskStatusDto.setStatusMsg(statusMsg);
                imcSendTaskStatusDto.setTaskId(taskId);
                imcSendTaskStatusDto.setUserId(userId);
                mqSendMsgDto.setUserId(userId);
                mqSendMsgDto.setMsgBodyDto(imcSendTaskStatusDto);
                msgBody = JSON.toJSONString(mqSendMsgDto);
            }else if(status==2){
                //发给服务商的消息
                imcSendTaskStatusDto.setStatus(status);
                imcSendTaskStatusDto.setStatusMsg(statusMsg);
                imcSendTaskStatusDto.setTaskId(taskId);
                imcSendTaskStatusDto.setUserId(facilitatorId);
                mqSendMsgDto.setUserId(facilitatorId);
                mqSendMsgDto.setMsgBodyDto(imcSendTaskStatusDto);
                msgBody = JSON.toJSONString(mqSendMsgDto);
            }else {
                //无此状态
                throw new BusinessException(ErrorCodeEnum.GL9999095);
            }
        }else{
            throw new BusinessException(ErrorCodeEnum.IMC10090006);
        }

        String topic = AliyunMqTopicConstants.MqTopicEnum.IMC_TOPIC.getTopic();
        String tag = AliyunMqTopicConstants.MqTagEnum.IMC_TASK_STATUS_CHANGED.getTag();
        String key = RedisKeyUtil.createMqKey(topic, tag, String.valueOf(taskId), msgBody);
        log.info("发送巡检任务状态改变消息，mqSendMsgDto={}",mqSendMsgDto);
        return new MqMessageData(msgBody, topic, tag, key);
    }

    /**
     * 通知服务商
     * @param undistributedImcTaskDto
     * @return
     */
    public MqMessageData sendNotifyMsgToFacilitator(UndistributedImcTaskDto undistributedImcTaskDto){
        Long facilitatorId = undistributedImcTaskDto.getFacilitatorId();
        Long taskId =undistributedImcTaskDto.getId();
        if(null!=facilitatorId){
            MqSendMsgDto<UndistributedImcTaskDto> mqSendMsgDto = new MqSendMsgDto<>();
            mqSendMsgDto.setMsgBodyDto(undistributedImcTaskDto);
            mqSendMsgDto.setUserId(facilitatorId);
            String msgBody = JSON.toJSONString(mqSendMsgDto);
            String topic = AliyunMqTopicConstants.MqTopicEnum.IMC_TOPIC.getTopic();
            String tag = AliyunMqTopicConstants.MqTagEnum.IMC_TASK_NOTIFY_FACILITATOR.getTag();
            String key = RedisKeyUtil.createMqKey(topic, tag, String.valueOf(taskId), msgBody);
            log.info("发送巡检任务服务商通知消息，mqSendMsgDto={}",mqSendMsgDto);
            return new MqMessageData(msgBody, topic, tag, key);
        }else{
            throw new BusinessException(ErrorCodeEnum.GL99990100);
        }
    }
}
