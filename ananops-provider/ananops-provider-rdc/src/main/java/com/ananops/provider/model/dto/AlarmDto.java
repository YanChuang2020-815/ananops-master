package com.ananops.provider.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/6/15 17:19
 */
@Data
public class AlarmDto implements Serializable {
    private static final long serialVersionUID = 9054408589592273658L;

    private Long deviceId;

    private Long sceneId;

    private double angle;
}
