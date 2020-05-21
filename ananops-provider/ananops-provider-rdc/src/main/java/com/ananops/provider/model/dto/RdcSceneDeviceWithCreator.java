package com.ananops.provider.model.dto;

import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by rongshuai on 2020/5/21 20:44
 */
@Data
public class RdcSceneDeviceWithCreator implements Serializable {
    private static final long serialVersionUID = 3410390496303416154L;

    private Long userId;

    private Long sceneId;

    private Long deviceId;

    /**
     * 设备在全景图中的经度
     */
    private BigDecimal longitude;

    /**
     * 设备在全景图中的纬度
     */
    private BigDecimal latitude;
}
