package com.ananops.provider.model.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Transient;
import java.io.Serializable;

/**
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020/4/12 下午1:25
 */
@Data
@ApiModel(value = "根据部门组织ID查询部门人员信息")
public class UserQueryDto implements Serializable {

    private static final long serialVersionUID = -2181894803727900830L;

    /**
     * 页码
     */
    @ApiModelProperty(value = "页码", required = true)
    private Integer pageNum;

    /**
     * 每页数量
     */
    @ApiModelProperty(value = "每页数量", required = true)
    private Integer pageSize;

    /**
     * 组织ID
     */
    @ApiModelProperty(value = "组织Id", required = true)
    private Long groupId;
}
