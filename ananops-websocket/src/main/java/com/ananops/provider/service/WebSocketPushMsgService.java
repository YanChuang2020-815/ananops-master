package com.ananops.provider.service;

import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.IService;
import com.ananops.provider.model.domain.WebsocketUserMessageInfo;
import com.ananops.provider.model.dto.WebSocketMsgDto;

/**
 * Created by rongshuai on 2020/2/19 19:27
 */
public interface WebSocketPushMsgService extends IService<WebsocketUserMessageInfo> {
    void SendMessageToWebSocketClient(WebSocketMsgDto webSocketMsgDto,String userId);
}
