package com.ananops.provider.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by rongshuai on 2020/5/21 20:25
 */
@Data
public class AlarmDeviceDto implements Serializable {
    private static final long serialVersionUID = 1511396017337717707L;

    private Long userId;

    private Long deviceId;

    private Long sceneId;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private Double value;

    private String deviceName;
}
