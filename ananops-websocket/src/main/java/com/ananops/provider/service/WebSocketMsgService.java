package com.ananops.provider.service;

import com.ananops.core.support.IService;
import com.ananops.provider.model.domain.WebsocketUserMessageInfo;
import com.ananops.provider.model.dto.*;
import com.github.pagehelper.PageInfo;

/**
 * Created by rongshuai on 2020/3/12 9:04
 */
public interface WebSocketMsgService extends IService<WebsocketUserMessageInfo> {

    PageInfo getMsgInfo(MsgQueryDto msgQueryDto);

    Integer changeMsgStatus(MsgStatusChangeDto msgStatusChangeDto);

    void test(AlarmDeviceDto alarmDeviceDto);

    void pushEdgeDeviceData(EdgeDeviceDataDto edgeDeviceDataDto);
}
