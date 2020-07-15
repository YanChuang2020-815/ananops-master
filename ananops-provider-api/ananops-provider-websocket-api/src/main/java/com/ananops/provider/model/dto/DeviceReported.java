package com.ananops.provider.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/7/15 19:19
 */
@Data
public class DeviceReported implements Serializable {
    private static final long serialVersionUID = -6013825933681271194L;

    private DeviceReportedMetadata metadata;

    private String value;
}
