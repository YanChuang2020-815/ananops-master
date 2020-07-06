package com.ananops.provider.service.impl;


import com.ananops.provider.model.device.DeviceList;
import com.ananops.provider.model.device.DoneableDevice;
import com.ananops.provider.model.device.EdgeDevice;
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

    public static String DEVICE_CRD_GROUP = "devices.kubeedge.io";
    public static String DEVICE_CRD_NAME = "devices." +  DEVICE_CRD_GROUP;

    @Override
    public void watchDeviceStatus(EdgeDevice edgeDevice) {
        CustomResourceDefinitionList crds = k8sClient.customResourceDefinitions().list();
        List<CustomResourceDefinition> crdsItems = crds.getItems();
        System.out.println("Found " + crdsItems.size() + " CRD(s)");
        CustomResourceDefinition deviceCRD = null;
        for (CustomResourceDefinition crd : crdsItems) {
            ObjectMeta metadata = crd.getMetadata();
            if (metadata != null) {
                String name = metadata.getName();
                System.out.println("    " + name + " => " + metadata.getSelfLink());
                if (DEVICE_CRD_NAME.equals(name)) {
                    deviceCRD = crd;
                }
            }
        }

        NonNamespaceOperation<EdgeDevice, DeviceList, DoneableDevice, Resource<EdgeDevice, DoneableDevice>> deviceClient =
                k8sClient.customResources(deviceCRD, EdgeDevice.class, DeviceList.class, DoneableDevice.class);

//        EdgeDevice created = new EdgeDevice();
//        ObjectMeta metadata = new ObjectMeta();
//        metadata.setName();
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
        CustomResourceDefinitionList crds = k8sClient.customResourceDefinitions().list();
        List<CustomResourceDefinition> crdsItems = crds.getItems();
        System.out.println("Found " + crdsItems.size() + " CRD(s)");
        CustomResourceDefinition deviceCRD = null;
        for (CustomResourceDefinition crd : crdsItems) {
            ObjectMeta metadata = crd.getMetadata();
            if (metadata != null) {
                String name = metadata.getName();
                System.out.println("    " + name + " => " + metadata.getSelfLink());
                if (DEVICE_CRD_NAME.equals(name)) {
                    deviceCRD = crd;
                }
            }
        }
        NonNamespaceOperation<EdgeDevice, DeviceList, DoneableDevice, Resource<EdgeDevice, DoneableDevice>> deviceClient =
                k8sClient.customResources(deviceCRD, EdgeDevice.class, DeviceList.class, DoneableDevice.class);
        CustomResourceList<EdgeDevice> deviceList = deviceClient.list();
        List<EdgeDevice> items = deviceList.getItems();
        return items;
    }
}
