package com.ananops.provider.service.impl;


import com.ananops.base.exception.BusinessException;
import com.ananops.provider.model.device.DeviceList;
import com.ananops.provider.model.device.DoneableDevice;
import com.ananops.provider.model.device.EdgeDevice;
import com.ananops.provider.model.deviceModel.DeviceModelList;
import com.ananops.provider.model.deviceModel.DoneableDeviceModel;
import com.ananops.provider.model.deviceModel.EdgeDeviceModel;
import com.ananops.provider.service.EdgeDeviceService;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinitionList;
import io.fabric8.kubernetes.client.CustomResourceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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


    @Override
    public void watchDeviceStatus(EdgeDevice edgeDevice) {
        deviceClient.withResourceVersion(edgeDevice.getMetadata().getResourceVersion()).watch(new Watcher<EdgeDevice>() {
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

    }

    @Override
    public List<EdgeDevice> getAllDevice() {
        CustomResourceList<EdgeDevice> deviceList = deviceClient.list();
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
