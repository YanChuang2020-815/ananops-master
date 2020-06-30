package com.ananops.provider.web.fronted;

import com.ananops.core.support.BaseController;
import com.ananops.provider.model.domain.Device;
import com.ananops.provider.model.domain.RdcScene;
import com.ananops.provider.model.dto.DeviceDataDto;
import com.ananops.provider.model.dto.RdcAddDeviceDto;
import com.ananops.provider.model.dto.RdcAddSceneDto;
import com.ananops.provider.model.vo.RdcDeviceVo;
import com.ananops.provider.model.vo.RdcSceneVo;
import com.ananops.provider.service.DeviceService;
import com.ananops.wrapper.WrapMapper;
import com.ananops.wrapper.Wrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by rongshuai on 2020/5/4 7:31
 */
@RestController
@RequestMapping(value = "/rdcDevice",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "WEB - RdcDevice",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class RdcDeviceController extends BaseController {
    @Resource
    DeviceService deviceService;

    @PostMapping(value = "/save")
    @ApiOperation(httpMethod = "POST",value = "编辑设备记录")
    public Wrapper<Device> saveRdcDevice(@ApiParam(name = "saveRdcDevice",value = "编辑设备")@RequestBody RdcAddDeviceDto rdcAddDeviceDto){
        logger.info("rdcAddDeviceDto={}",rdcAddDeviceDto);
        return WrapMapper.ok(deviceService.createDevice(getLoginAuthDto(),rdcAddDeviceDto));
    }

    @GetMapping(value = "/getAllDevice")
    @ApiOperation(httpMethod = "GET", value = "查询当前用户组织下全部的设备信息")
    public Wrapper<List<RdcDeviceVo>> getAllDevice(){
        return WrapMapper.ok(deviceService.getAllDevice(getLoginAuthDto()));
    }

    @DeleteMapping(value = "/deleteDevice/{deviceId}")
    @ApiOperation(httpMethod = "DELETE", value = "删除设备")
    public Wrapper deleteDevice(@PathVariable Long deviceId){
        deviceService.deleteDevice(deviceId);
        return WrapMapper.ok();
    }

    @GetMapping(value = "/getAllDeviceByUserAndScene/{sceneId}")
    @ApiOperation(httpMethod = "GET", value = "查询当前用户组织下，指定场景下的全部的设备信息")
    public Wrapper<List<RdcDeviceVo>> getAllDeviceByUserAndScene(@PathVariable("sceneId") Long sceneId) {
        return WrapMapper.ok(deviceService.getAllDeviceByUserAndScene(getLoginAuthDto(),sceneId));
    }

    @PostMapping(value = "/pushDeviceData")
    @ApiOperation(httpMethod = "POST",value = "发送设备数据")
    public Wrapper pushDeviceData(@RequestBody DeviceDataDto deviceDataDto) {
        deviceService.pushDeviceData(deviceDataDto);
        return WrapMapper.ok();
    }
}
