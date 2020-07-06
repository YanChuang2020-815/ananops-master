package com.ananops.provider.model.deviceModel;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import lombok.Data;

/**
 * Created by rongshuai on 2020/7/6 11:51
 */
@Data
public class EdgeDeviceModel extends CustomResource implements Namespaced {
    private static final long serialVersionUID = -830277060548487449L;

    private DeviceModelSpec spec;
}
