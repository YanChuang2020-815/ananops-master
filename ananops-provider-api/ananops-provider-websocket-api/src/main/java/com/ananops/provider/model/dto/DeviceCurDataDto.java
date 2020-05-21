package com.ananops.provider.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by rongshuai on 2020/5/21 20:02
 */
@Data
public class DeviceCurDataDto implements Serializable {
    private static final long serialVersionUID = -1552264229270670061L;

    private Date ts;

    private String key;

    private Double value;
}
