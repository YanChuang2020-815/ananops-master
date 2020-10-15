package com.ananops.provider.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rongshuai on 2020/10/15 21:27
 */
@Data
public class EdgeDeviceCreateDto implements Serializable {
    private static final long serialVersionUID = -6147487902493688926L;

    private String deviceName;

    private String description;

    private String model;

    private String deviceModelRefName;

    private String nodeName;

    private List<EdgeDeviceTwinDto> deviceTwinDtoList;


}
