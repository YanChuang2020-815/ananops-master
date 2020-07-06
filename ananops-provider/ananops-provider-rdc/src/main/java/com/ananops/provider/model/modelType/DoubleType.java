package com.ananops.provider.model.modelType;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/7/6 14:25
 */
@Data
public class DoubleType implements Serializable {
    private static final long serialVersionUID = -5206826574838792202L;

    private String accessMode;

    private double defaultValue;
}
