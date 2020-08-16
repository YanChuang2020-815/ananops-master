package com.ananops.provider.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rongshuai on 2020/8/16 20:04
 */
@Data
public class EdgeCurDataDto implements Serializable {
    private static final long serialVersionUID = 5494005092909311940L;

    private String deviceName;

    private List<SingleDataDto> dataList;
}
