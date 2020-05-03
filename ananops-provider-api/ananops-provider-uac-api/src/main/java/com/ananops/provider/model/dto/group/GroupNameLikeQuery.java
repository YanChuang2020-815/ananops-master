package com.ananops.provider.model.dto.group;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020-02-24 13:41
 */
@Data
@ApiModel(value = "模糊查询组织Dto")
public class GroupNameLikeQuery implements Serializable {

    private static final long serialVersionUID = 876251006897670727L;

    /**
     * 组织类型
     */
    @ApiModelProperty(value = "组织类型")
    private String type;

    /**
     * 需要查找的组织名称
     */
    @ApiModelProperty(value = "需要查找的组织名称")
    private String groupName;
}
