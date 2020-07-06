package com.ananops.provider.model.modelType;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/7/6 11:55
 */
@Data
public class IntType implements Serializable {
    private static final long serialVersionUID = 2872619384466465235L;

    private String accessMode;

    private int defaultValue;
}
