package com.ananops.provider.model.modelType;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/7/6 14:25
 */
@Data
public class StringType implements Serializable {
    private static final long serialVersionUID = 6531419802195273667L;

    private String accessMode;

    private String defaultValue;
}
