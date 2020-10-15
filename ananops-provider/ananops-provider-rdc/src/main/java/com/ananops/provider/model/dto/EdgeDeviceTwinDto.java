package com.ananops.provider.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/10/15 21:32
 */
@Data
public class EdgeDeviceTwinDto implements Serializable{
    private static final long serialVersionUID = -2917537376977893712L;

    private String propertyName;

    private String requireType;

    private String requireValue;

    private String reportedType;

    private String reportedValue;

    private String reportedTime;
}
