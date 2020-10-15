package com.ananops.provider.web.fronted;


import com.ananops.provider.model.device.DeviceModelRef;
import com.ananops.provider.model.device.DeviceSpec;
import com.ananops.provider.model.device.DeviceStatus;
import com.ananops.provider.model.device.EdgeDevice;
import com.ananops.provider.model.deviceModel.EdgeDeviceModel;
import com.ananops.provider.model.dto.*;
import com.ananops.provider.service.EdgeDeviceService;
import com.ananops.wrapper.WrapMapper;
import com.ananops.wrapper.Wrapper;
import io.fabric8.kubernetes.api.model.NodeSelector;
import io.fabric8.kubernetes.api.model.NodeSelectorRequirement;
import io.fabric8.kubernetes.api.model.NodeSelectorTerm;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        EdgeDeviceCreateDto deviceCreateDto = new EdgeDeviceCreateDto();
        EdgeDevice device = new EdgeDevice();
        DeviceSpec deviceSpec = new DeviceSpec();
        DeviceModelRef deviceModelRef = new DeviceModelRef();
        deviceModelRef.setName(deviceCreateDto.getDeviceModelRefName());
        deviceSpec.setDeviceModelRef(deviceModelRef);
        NodeSelector nodeSelector = new NodeSelector();
        List<NodeSelectorTerm> nodeSelectorTerms = new ArrayList<>();
        NodeSelectorTerm nodeSelectorTerm = new NodeSelectorTerm();
        List<NodeSelectorRequirement> nodeSelectorRequirements = new ArrayList<>();
        NodeSelectorRequirement nodeSelectorRequirement = new NodeSelectorRequirement();
        nodeSelectorRequirement.setOperator("In");
        List<String> nodes = new ArrayList<>();
        nodes.add(deviceCreateDto.getNodeName());
        nodeSelectorRequirement.setValues(nodes);
        nodeSelectorRequirements.add(0,nodeSelectorRequirement);
        nodeSelectorTerm.setMatchExpressions(nodeSelectorRequirements);
        nodeSelectorTerms.add(0,nodeSelectorTerm);
        nodeSelector.setNodeSelectorTerms(nodeSelectorTerms);
        deviceSpec.setNodeSelector(nodeSelector);
        device.setSpec(deviceSpec);
        DeviceStatus deviceStatus = new DeviceStatus();
        List<DeviceTwin> deviceTwins = new ArrayList<>();
        deviceCreateDto.getDeviceTwinDtoList().forEach(edgeDeviceTwin -> {
            DeviceTwin deviceTwin = new DeviceTwin();
            deviceTwin.setPropertyName(edgeDeviceTwin.getPropertyName());
            DeviceDesired deviceDesired = new DeviceDesired();
            DeviceDesiredMetadata desiredMetadata = new DeviceDesiredMetadata();
            desiredMetadata.setType(edgeDeviceTwin.getRequireType());
            deviceDesired.setMetadata(desiredMetadata);
            deviceDesired.setValue(edgeDeviceTwin.getRequireValue());
            deviceTwin.setDesired(deviceDesired);
            deviceTwins.add(deviceTwin);
        });
        deviceStatus.setTwins(deviceTwins);
        device.setStatus(deviceStatus);
        device.setApiVersion("devices.kubeedge.io/v1alpha1");
        device.setKind("Device");
        ObjectMeta objectMeta = new ObjectMeta();
        objectMeta.setName(deviceCreateDto.getDeviceName());
        Map<String,String> map = new HashMap<>();
        map.put("description",deviceCreateDto.getDescription());
        map.put("model",deviceCreateDto.getModel());
        objectMeta.setLabels(map);
        device.setMetadata(objectMeta);
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
