package com.ananops.provider.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rongshuai on 2020/5/21 19:58
 */
@Data
public class DeviceDataDto implements Serializable {
    private static final long serialVersionUID = -8012248727255268238L;

    private String deviceId;

    private int tenantId;

    private String gatewayId;

    private String name;

    private List<DeviceCurDataDto> data;
}
