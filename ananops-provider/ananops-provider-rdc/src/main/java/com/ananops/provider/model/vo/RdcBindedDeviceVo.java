package com.ananops.provider.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by rongshuai on 2020/5/4 10:28
 */
@Data
@ApiModel
public class RdcBindedDeviceVo implements Serializable {
    private static final long serialVersionUID = 8826707089072550098L;

    private Long sceneId;

    private Long deviceId;

    private String name;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String url;
}
