package com.ananops.provider.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020-04-07 16:24
 */
@Data
@ApiModel
public class SysDictItemsDto implements Serializable {

    private static final long serialVersionUID = 3039996350069041194L;

    /**
     * 故障类型
     */
    private List<DictItemDto> troubleTypeList;

    /**
     * 故障位置
     */
    private List<DictItemDto> troubleAddressList;

    /**
     * 设备类型
     */
    private List<DictItemDto> deviceTypeList;

    /**
     * 故障等级
     */
    private List<DictItemDto> troubleLevelList;

    /**
     * 紧急程度
     */
    private List<DictItemDto> emergencyLevelList;
}
