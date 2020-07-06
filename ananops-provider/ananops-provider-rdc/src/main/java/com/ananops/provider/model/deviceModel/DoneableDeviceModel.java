package com.ananops.provider.model.deviceModel;

import io.fabric8.kubernetes.api.builder.Function;
import io.fabric8.kubernetes.client.CustomResourceDoneable;

/**
 * Created by rongshuai on 2020/7/6 14:50
 */
public class DoneableDeviceModel extends CustomResourceDoneable<EdgeDeviceModel> {
    public DoneableDeviceModel(EdgeDeviceModel resource, Function function) {
        super(resource, function);
    }
}
