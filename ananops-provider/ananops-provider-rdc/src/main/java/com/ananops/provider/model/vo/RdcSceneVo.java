package com.ananops.provider.model.vo;

import com.ananops.provider.model.dto.oss.ElementImgUrlDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/4/27 23:39
 */
@Data
@ApiModel
public class RdcSceneVo implements Serializable {
    private static final long serialVersionUID = -2530709489824169731L;

    @ApiModelProperty("场景唯一ID")
    private Long id;

    @ApiModelProperty("场景名称")
    private String sceneName;

    @ApiModelProperty("场景图片信息")
    private String url;

}
