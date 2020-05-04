package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/5/4 11:07
 */
@Data
@ApiModel
public class RdcSceneDeviceQueryDto implements Serializable {
    private static final long serialVersionUID = -8817825870577402792L;

    private Long sceneId;

    private Long deviceId;
}
