package com.ananops.provider.mqConsumer;

import com.ananops.provider.model.dto.AlarmDeviceDto;
import com.ananops.provider.model.dto.DeviceDataDto;
import com.ananops.provider.service.RdcSceneService;
import com.ananops.provider.service.WebSocketFeignApi;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by rongshuai on 2020/5/21 20:11
 */
@Service
public class MsgProcesser {
    @Resource
    RdcSceneService rdcSceneService;

    @Resource
    WebSocketFeignApi webSocketFeignApi;

    @Async
    public void msgProcess(DeviceDataDto deviceDataDto){
        AlarmDeviceDto alarmDeviceDto = rdcSceneService.getAlarmDevice(deviceDataDto);

        if(null!=alarmDeviceDto){
            webSocketFeignApi.pushMsg(alarmDeviceDto);
        }

    }

}
