package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 项目列表查询参数
 *
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020-04-08 10:48
 */
@Data
@ApiModel
public class PmcProReqQueryDto implements Serializable {

    private static final long serialVersionUID = 8336639667959028926L;

    /**
     * 公司组织Id
     */
    private Long groupId;

    /**
     * 项目类型
     */
    private String projectType;
}
