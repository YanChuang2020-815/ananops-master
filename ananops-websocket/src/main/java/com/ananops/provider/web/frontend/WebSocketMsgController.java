package com.ananops.provider.web.frontend;

import com.ananops.core.support.BaseController;
import com.ananops.provider.model.dto.MsgQueryDto;
import com.ananops.provider.model.dto.MsgStatusChangeDto;
import com.ananops.provider.service.WebSocketMsgService;
import com.ananops.wrapper.WrapMapper;
import com.ananops.wrapper.Wrapper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by rongshuai on 2020/3/11 23:33
 */
@RestController
@RequestMapping(value = "/websocket",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "WEB - WebSocketMsg",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WebSocketMsgController extends BaseController {

    @Resource
    WebSocketMsgService webSocketMsgService;

    @PostMapping(value = "/queryWebsocketMsgInfo")
    @ApiOperation(httpMethod = "POST",value = "查询websocket消息")
    public Wrapper<PageInfo> queryWebsocketMsgInfo(@ApiParam(name = "queryWebsocketMsgInfo",value = "查询websocket消息")@RequestBody MsgQueryDto msgQueryDto){
        logger.info("查询websocket消息：msgQueryDto={}",msgQueryDto);
        return WrapMapper.ok(webSocketMsgService.getMsgInfo(msgQueryDto));
    }

    @PostMapping(value = "/changeWebsocketMsgStatus")
    @ApiOperation(httpMethod = "POST",value = "修改websocket消息的状态")
    public Wrapper<String> changeWebsocketMsgStatus(@ApiParam(name = "changeWebsocketMsgStatus",value = "修改websocket消息的状态")@RequestBody MsgStatusChangeDto msgStatusChangeDto){
        logger.info("修改websocket消息的状态：msgStatusChangeDto={}",msgStatusChangeDto);
        int result = webSocketMsgService.changeMsgStatus(msgStatusChangeDto);
        if(result==1){
            return WrapMapper.ok("修改消息状态成功");
        }else{
            return WrapMapper.ok("修改消息状态失败");
        }
    }
}
