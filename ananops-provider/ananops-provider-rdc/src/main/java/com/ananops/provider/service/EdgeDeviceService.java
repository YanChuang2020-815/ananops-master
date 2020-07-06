package com.ananops.provider.service;


import com.ananops.provider.model.device.EdgeDevice;

import java.util.List;

/**
 * Created by rongshuai on 2020/7/2 15:27
 */
public interface EdgeDeviceService {
    void watchDeviceStatus(EdgeDevice edgeDevice);

    List<EdgeDevice> getAllDevice();
}
