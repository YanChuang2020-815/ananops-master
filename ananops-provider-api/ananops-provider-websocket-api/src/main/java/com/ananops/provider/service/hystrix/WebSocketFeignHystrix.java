package com.ananops.provider.service.hystrix;

import com.ananops.provider.model.dto.AlarmDeviceDto;
import com.ananops.provider.model.dto.EdgeDeviceDataDto;
import com.ananops.provider.model.dto.MsgDto;
import com.ananops.provider.service.WebSocketFeignApi;
import com.ananops.wrapper.Wrapper;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by rongshuai on 2020/5/21 19:16
 */
@Component
public class WebSocketFeignHystrix implements WebSocketFeignApi {

    @Override
    public Wrapper pushMsg(@ApiParam(name = "getTaskByFacilitatorId", value = "根据服务商ID查询巡检任务") @RequestBody AlarmDeviceDto alarmDeviceDto){
        return null;
    }

    @Override
    public Wrapper pushEdgeDeviceData(@RequestBody EdgeDeviceDataDto edgeDeviceDataDto) {
        return null;
    }
}
