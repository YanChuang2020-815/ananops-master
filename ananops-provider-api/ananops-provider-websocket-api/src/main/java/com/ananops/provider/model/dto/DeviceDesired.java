package com.ananops.provider.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/7/2 18:41
 */
@Data
public class DeviceDesired implements Serializable {
    private static final long serialVersionUID = -1804375678200837374L;

    private DeviceDesiredMetadata metadata;

    private String value;
}
