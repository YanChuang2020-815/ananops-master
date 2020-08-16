package com.ananops.provider.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/8/16 20:04
 */
@Data
public class SingleDataDto implements Serializable {
    private static final long serialVersionUID = -2191572897400837792L;

    private String name;

    private String value;
}
