package com.ananops.provider.config;



import com.ananops.provider.model.device.DeviceList;
import com.ananops.provider.model.device.DoneableDevice;
import com.ananops.provider.model.device.EdgeDevice;
import com.ananops.provider.model.deviceModel.DeviceModelList;
import com.ananops.provider.model.deviceModel.DoneableDeviceModel;
import com.ananops.provider.model.deviceModel.EdgeDeviceModel;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinitionList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by rongshuai on 2020/7/6 16:01
 */
@Data
@Configuration
public class K8sDeviceConfig {
    @Autowired
    private KubernetesClient k8sClient;

    public static String DEVICE_CRD_GROUP = "devices.kubeedge.io";
    public static String DEVICE_CRD_NAME = "devices." +  DEVICE_CRD_GROUP;
    public static String DEVICE_MODEL_CRD_NAME = "devicemodels." + DEVICE_CRD_GROUP;

    @Bean
    public NonNamespaceOperation<EdgeDevice, DeviceList, DoneableDevice, Resource<EdgeDevice, DoneableDevice>> deviceClient()
        throws KubernetesClientException {
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

        return k8sClient.customResources(deviceCRD, EdgeDevice.class, DeviceList.class, DoneableDevice.class);
    }

    @Bean
    public NonNamespaceOperation<EdgeDeviceModel, DeviceModelList, DoneableDeviceModel, Resource<EdgeDeviceModel, DoneableDeviceModel>> deviceModelClient()
        throws KubernetesClientException {
        CustomResourceDefinitionList crds = k8sClient.customResourceDefinitions().list();
        List<CustomResourceDefinition> crdsItems = crds.getItems();
        System.out.println("Found " + crdsItems.size() + " CRD(s)");
        CustomResourceDefinition deviceModelCRD = null;
        for (CustomResourceDefinition crd : crdsItems) {
            ObjectMeta metadata = crd.getMetadata();
            if (metadata != null) {
                String name = metadata.getName();
                System.out.println("    " + name + " => " + metadata.getSelfLink());
                if (DEVICE_MODEL_CRD_NAME.equals(name)) {
                    deviceModelCRD = crd;
                }
            }
        }
        return k8sClient.customResources(deviceModelCRD, EdgeDeviceModel.class, DeviceModelList.class, DoneableDeviceModel.class);
    }
}
