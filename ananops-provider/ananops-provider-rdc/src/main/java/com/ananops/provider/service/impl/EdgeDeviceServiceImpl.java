package com.ananops.provider.service.impl;


import com.ananops.base.exception.BusinessException;
import com.ananops.provider.model.device.DeviceList;
import com.ananops.provider.model.device.DoneableDevice;
import com.ananops.provider.model.device.EdgeDevice;
import com.ananops.provider.model.deviceModel.DeviceModelList;
import com.ananops.provider.model.deviceModel.DoneableDeviceModel;
import com.ananops.provider.model.deviceModel.EdgeDeviceModel;
import com.ananops.provider.model.dto.EdgeDeviceDataDto;
import com.ananops.provider.service.EdgeDeviceService;
import com.ananops.provider.service.WebSocketFeignApi;
import io.fabric8.kubernetes.client.*;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rongshuai on 2020/7/2 15:28
 */
@Service
@Slf4j
public class EdgeDeviceServiceImpl implements EdgeDeviceService {

    @Autowired
    private KubernetesClient k8sClient;

    @Autowired
    private NonNamespaceOperation<EdgeDevice, DeviceList, DoneableDevice, Resource<EdgeDevice, DoneableDevice>> deviceClient;

    @Autowired
    private NonNamespaceOperation<EdgeDeviceModel, DeviceModelList, DoneableDeviceModel, Resource<EdgeDeviceModel, DoneableDeviceModel>> deviceModelClient;

    @Autowired
    private WebSocketFeignApi webSocketFeignApi;

    private ConcurrentHashMap<String, Watch> watchMap = new ConcurrentHashMap<>();

    @Override
    public void watchDeviceStatus(EdgeDevice edgeDevice) {
        if (!watchMap.containsKey(edgeDevice.getMetadata().getUid())) {
            Watch watch = deviceClient.withResourceVersion(edgeDevice.getMetadata().getResourceVersion()).watch(new Watcher<EdgeDevice>() {
                @Override
                public void eventReceived(Action action, EdgeDevice resource) {
                    System.out.println("==> " + action + " for " + resource);
                    if (resource.getSpec() == null) {
                        log.error("No Spec for resource " + resource);
                    }
                }

                @Override
                public void onClose(KubernetesClientException cause) {
                }
            });
            watchMap.put(edgeDevice.getMetadata().getUid(),watch);
        }

    }

    @Override
    public List<EdgeDevice> getAllDevice() {
        CustomResourceList<EdgeDevice> deviceList = deviceClient.list();
        deviceList.getItems().forEach(edgeDevice ->
                {
                    if (!watchMap.containsKey(edgeDevice.getMetadata().getUid())) {
                        Watch watch = deviceClient.withResourceVersion(edgeDevice.getMetadata().getResourceVersion()).watch(new Watcher<EdgeDevice>() {
                            @Override
                            public void eventReceived(Action action, EdgeDevice resource) {
                                System.out.println("==> " + action + " for " + resource);
                                EdgeDeviceDataDto edgeDeviceDataDto = new EdgeDeviceDataDto();
                                edgeDeviceDataDto.setDeviceTwins(edgeDevice.getStatus().getTwins());
                                edgeDeviceDataDto.setUserId(Long.parseLong("896330256212820992"));
                                webSocketFeignApi.pushEdgeDeviceData(edgeDeviceDataDto);
                                if (resource.getSpec() == null) {
                                    log.error("No Spec for resource " + resource);
                                }
                            }

                            @Override
                            public void onClose(KubernetesClientException cause) {
                            }
                        });
                        watchMap.put(edgeDevice.getMetadata().getUid(),watch);
                    }
                }
        );
        return deviceList.getItems();
    }

    @Override
    public List<EdgeDeviceModel> getAllDeviceModel() {
        CustomResourceList<EdgeDeviceModel> deviceModelList = deviceModelClient.list();
        return deviceModelList.getItems();
    }

    @Override
    public void createEdgeDeviceModel(EdgeDeviceModel edgeDeviceModel) {
        try {
            deviceModelClient.createOrReplace(edgeDeviceModel);
        } catch (Exception e) {
            throw new BusinessException("创建设备模型失败");
        }
    }

    @Override
    public void createEdgeDevice(EdgeDevice edgeDevice) {
        try {
            deviceClient.createOrReplace(edgeDevice);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("创建设备失败");
        }
    }

    @Override
    public void deleteEdgeDevice(EdgeDevice edgeDevice) {
        try {
            deviceClient.delete(edgeDevice);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("设备删除失败");
        }
    }

    @Override
    public void deleteEdgeDeviceModel(EdgeDeviceModel edgeDeviceModel) {
        try {
            deviceModelClient.delete(edgeDeviceModel);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("设备删除失败");
        }
    }
}
