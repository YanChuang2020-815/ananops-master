package com.ananops.provider.web.fronted;


import com.ananops.provider.model.device.EdgeDevice;
import com.ananops.provider.model.deviceModel.EdgeDeviceModel;
import com.ananops.provider.service.EdgeDeviceService;
import com.ananops.wrapper.WrapMapper;
import com.ananops.wrapper.Wrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by rongshuai on 2020/7/2 15:25
 */
@RestController
@RequestMapping("edgeDevice")
@Api(value = "k8s",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class EdgeDeviceController {
    @Resource
    private EdgeDeviceService edgeDeviceService;

    @GetMapping("getAllEdgeDevice")
    @ApiOperation( "获取全部设备")
    public Wrapper<List<EdgeDevice>> getAllDevice() {
        return WrapMapper.ok(edgeDeviceService.getAllDevice());
    }

    @PostMapping("watchEdgeDevice")
    @ApiOperation( "监听指定设备")
    public void watchDevice(@RequestBody EdgeDevice edgeDevice) {
        edgeDeviceService.watchDeviceStatus(edgeDevice);
    }

    @GetMapping("getAllEdgeDeviceModel")
    @ApiOperation("获取全部设备模型")
    public Wrapper<List<EdgeDeviceModel>> getAllEdgeDeviceModel() {
        return WrapMapper.ok(edgeDeviceService.getAllDeviceModel());
    }

    @PostMapping("createEdgeDevice")
    @ApiOperation("创建设备")
    public Wrapper createEdgeDevice(@RequestBody EdgeDevice edgeDevice) {
        edgeDeviceService.createEdgeDevice(edgeDevice);
        return WrapMapper.ok();
    }

    @PostMapping("createEdgeDeviceModel")
    @ApiOperation("创建设备模型")
    public Wrapper createEdgeDeviceModel(@RequestBody EdgeDeviceModel edgeDeviceModel) {
        edgeDeviceService.createEdgeDeviceModel(edgeDeviceModel);
        return WrapMapper.ok();
    }

    @PostMapping("deleteEdgeDevice")
    @ApiOperation("删除设备")
    public Wrapper deleteEdgeDevice(@RequestBody EdgeDevice edgeDevice) {
        edgeDeviceService.deleteEdgeDevice(edgeDevice);
        return WrapMapper.ok();
    }

    @PostMapping("deleteEdgeDeviceModel")
    @ApiOperation("删除设备模型")
    public Wrapper deleteEdgeDeviceModel(@RequestBody EdgeDeviceModel edgeDeviceModel) {
        edgeDeviceService.deleteEdgeDeviceModel(edgeDeviceModel);
        return WrapMapper.ok();
    }
}
