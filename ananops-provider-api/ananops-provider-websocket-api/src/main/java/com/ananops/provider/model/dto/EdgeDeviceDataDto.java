package com.ananops.provider.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rongshuai on 2020/7/7 18:53
 */
@Data
public class EdgeDeviceDataDto implements Serializable {
    private static final long serialVersionUID = 7576192039029230385L;

    private Long userId;

    private List<DeviceTwin> deviceTwins;

    private String action;

    private String deviceName;
}
