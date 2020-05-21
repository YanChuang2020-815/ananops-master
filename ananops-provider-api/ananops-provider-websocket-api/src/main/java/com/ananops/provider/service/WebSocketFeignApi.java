package com.ananops.provider.service;

import com.ananops.provider.model.dto.AlarmDeviceDto;
import com.ananops.provider.model.dto.MsgDto;
import com.ananops.provider.service.hystrix.WebSocketFeignHystrix;
import com.ananops.security.feign.OAuth2FeignAutoConfiguration;
import com.ananops.wrapper.Wrapper;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by rongshuai on 2020/5/21 19:15
 */
@FeignClient(value = "ananops-provider-websocket", configuration = OAuth2FeignAutoConfiguration.class, fallback = WebSocketFeignHystrix.class)
public interface WebSocketFeignApi {
    @PostMapping(value = "/api/websocket/updateMsg")
    Wrapper pushMsg(@ApiParam(name = "getTaskByFacilitatorId",value = "根据服务商ID查询巡检任务")@RequestBody AlarmDeviceDto alarmDeviceDto);
}
