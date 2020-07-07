package com.ananops.provider.service;


import com.ananops.provider.model.device.EdgeDevice;
import com.ananops.provider.model.deviceModel.EdgeDeviceModel;

import java.util.List;

/**
 * Created by rongshuai on 2020/7/2 15:27
 */
public interface EdgeDeviceService {
    void watchDeviceStatus(EdgeDevice edgeDevice);

    List<EdgeDevice> getAllDevice();

    List<EdgeDeviceModel> getAllDeviceModel();

    void createEdgeDeviceModel(EdgeDeviceModel edgeDeviceModel);

    void createEdgeDevice(EdgeDevice edgeDevice);

    void deleteEdgeDevice(EdgeDevice edgeDevice);

    void deleteEdgeDeviceModel(EdgeDeviceModel edgeDeviceModel);
}
