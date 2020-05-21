package com.ananops.provider.web.rpc;

import com.ananops.core.support.BaseController;
import com.ananops.provider.model.dto.AlarmDeviceDto;
import com.ananops.provider.model.dto.MsgDto;
import com.ananops.provider.model.dto.TaskDto;
import com.ananops.provider.model.dto.TaskQueryDto;
import com.ananops.provider.service.ImcTaskFeignApi;
import com.ananops.provider.service.WebSocketFeignApi;
import com.ananops.provider.service.WebSocketMsgService;
import com.ananops.wrapper.WrapMapper;
import com.ananops.wrapper.Wrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rongshuai on 2020/5/21 19:24
 */
@RefreshScope
@RestController
@Api(value = "API - WebSocketFeignClient", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class MsgPushController extends BaseController implements WebSocketFeignApi {

    @Resource
    WebSocketMsgService webSocketMsgService;

    @Override
    @ApiOperation(httpMethod = "POST", value = "根据服务商ID查询巡检任务")
    public Wrapper pushMsg(@ApiParam(name = "getTaskByFacilitatorId",value = "根据服务商ID查询巡检任务")@RequestBody AlarmDeviceDto alarmDeviceDto){
        webSocketMsgService.test(alarmDeviceDto);
        return WrapMapper.ok();
    };

}
