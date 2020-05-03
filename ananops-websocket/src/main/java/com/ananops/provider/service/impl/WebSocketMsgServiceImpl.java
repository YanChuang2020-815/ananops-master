package com.ananops.provider.service.impl;

import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.base.exception.BusinessException;
import com.ananops.core.support.BaseService;
import com.ananops.provider.mapper.WebsocketUserMessageInfoMapper;
import com.ananops.provider.model.domain.WebsocketUserMessageInfo;
import com.ananops.provider.model.dto.MsgQueryDto;
import com.ananops.provider.model.dto.MsgStatusChangeDto;
import com.ananops.provider.service.WebSocketMsgService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by rongshuai on 2020/3/12 9:04
 */
@Service
public class WebSocketMsgServiceImpl extends BaseService<WebsocketUserMessageInfo> implements WebSocketMsgService {
    @Resource
    WebsocketUserMessageInfoMapper websocketUserMessageInfoMapper;

    /**
     * 消息信息查询
     * @param msgQueryDto
     * @return
     */
    @Override
    public PageInfo getMsgInfo(MsgQueryDto msgQueryDto){
        Long userId = msgQueryDto.getUserId();
        Integer status = msgQueryDto.getStatus();
        String messageTopic = msgQueryDto.getMessageTopic();
        String messageTag = msgQueryDto.getMessageTag();
        PageHelper.startPage(msgQueryDto.getPageNum(),msgQueryDto.getPageSize());
        List<WebsocketUserMessageInfo> websocketUserMessageInfos
                = websocketUserMessageInfoMapper.getMsgInfo(userId,status,messageTopic,messageTag);
        return new PageInfo<>(websocketUserMessageInfos);
    }

    /**
     * 修改消息状态
     * @param msgStatusChangeDto
     * @return
     */
    @Override
    public Integer changeMsgStatus(MsgStatusChangeDto msgStatusChangeDto){
        Long messageId = msgStatusChangeDto.getMessageId();
        if(messageId!=null){
            Integer status = msgStatusChangeDto.getStatus();
            if(status==null){
                throw new BusinessException(ErrorCodeEnum.GL99990100);
            }
            WebsocketUserMessageInfo websocketUserMessageInfo
                    = websocketUserMessageInfoMapper.selectByPrimaryKey(messageId);
            if(websocketUserMessageInfo!=null){
                websocketUserMessageInfo.setStatus(status);
                websocketUserMessageInfo.setUpdateTime(new Date());
                return websocketUserMessageInfoMapper.updateByPrimaryKeySelective(websocketUserMessageInfo);
            }else{
                throw new BusinessException(ErrorCodeEnum.WEBSOCKET10100002);
            }
        }else{
            throw new BusinessException(ErrorCodeEnum.GL99990100);
        }
    }

}
