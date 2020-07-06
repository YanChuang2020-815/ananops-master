package com.ananops.provider.model.device;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/7/2 18:20
 */
@Data
public class DeviceModelRef implements Serializable {
    private static final long serialVersionUID = 1826504912295673383L;

    private String name;
}
