package com.ananops.provider.service.impl;

import com.ananops.JacksonUtil;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.base.exception.BusinessException;
import com.ananops.core.config.PcObjectMapper;
import com.ananops.core.support.BaseService;
import com.ananops.provider.mapper.WebsocketUserMessageInfoMapper;
import com.ananops.provider.model.domain.WebsocketUserMessageInfo;
import com.ananops.provider.model.dto.WebSocketMsgDto;
import com.ananops.provider.service.WebSocketPushMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by rongshuai on 2020/2/19 19:28
 */
@Service
public class WebSocketPushMsgServiceImpl extends BaseService<WebsocketUserMessageInfo> implements WebSocketPushMsgService {
    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @Resource
    WebsocketUserMessageInfoMapper websocketUserMessageInfoMapper;


    @Override
    public void SendMessageToWebSocketClient(WebSocketMsgDto webSocketMsgDto, String userId){
        if(userId!=null){
            try{
                Long messageId = super.generateId();
                String body = PcObjectMapper.getObjectMapper().writeValueAsString(webSocketMsgDto.getContent());//JacksonUtil.toJsonWithFormat(webSocketMsgDto.getContent());
                WebsocketUserMessageInfo websocketUserMessageInfo = new WebsocketUserMessageInfo();
                websocketUserMessageInfo.setMessageBody(body);
                websocketUserMessageInfo.setMessageTag(webSocketMsgDto.getTag());
                websocketUserMessageInfo.setMessageTopic(webSocketMsgDto.getTopic());
                websocketUserMessageInfo.setStatus(0);
                websocketUserMessageInfo.setUserId(Long.parseLong(userId));
                websocketUserMessageInfo.setId(messageId);
                websocketUserMessageInfo.setCreatedTime(new Date());
                websocketUserMessageInfo.setUpdateTime(new Date());
                websocketUserMessageInfoMapper.insert(websocketUserMessageInfo);
                logger.info("插入websocket消息：websocketUserMessageInfo={}",websocketUserMessageInfo);
                //发送单对单WebSocket消息
                webSocketMsgDto.setMessageId(messageId);
                messagingTemplate.convertAndSendToUser(userId,"/queue/chat",webSocketMsgDto);
            }catch (Exception e){
                throw new BusinessException(ErrorCodeEnum.WEBSOCKET10100001);
            }
        }
    }

}
