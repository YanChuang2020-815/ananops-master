package com.ananops.provider.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/7/15 19:20
 */
@Data
public class DeviceReportedMetadata implements Serializable {
    private static final long serialVersionUID = 8418614261961675662L;

    private String timestamp;

    private String type;
}
