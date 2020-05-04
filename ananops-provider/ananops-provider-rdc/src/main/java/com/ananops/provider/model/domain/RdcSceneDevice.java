package com.ananops.provider.model.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by rongshuai on 2020/5/3 21:34
 */
@Data
@Table(name = "an_rdc_scene_device")
public class RdcSceneDevice implements Serializable {
    private static final long serialVersionUID = 7579093359823743260L;

    @Column(name = "scene_id")
    private Long sceneId;

    @Column(name = "device_id")
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
