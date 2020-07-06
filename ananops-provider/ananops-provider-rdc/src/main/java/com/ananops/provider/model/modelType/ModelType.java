package com.ananops.provider.model.modelType;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/7/6 14:47
 */
@Data
public class ModelType implements Serializable {
    private static final long serialVersionUID = 5444916569473644850L;

    @JsonProperty("int")
    private IntType intType;

    @JsonProperty("string")
    private StringType stringType;

    @JsonProperty("double")
    private DoubleType doubleType;
}
