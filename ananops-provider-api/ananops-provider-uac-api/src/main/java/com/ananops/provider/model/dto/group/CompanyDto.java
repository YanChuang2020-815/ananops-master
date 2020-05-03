package com.ananops.provider.model.dto.group;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.beans.Transient;
import java.io.Serializable;
import java.util.List;

/**
 * Created By ChengHao On 2020/2/26
 */
@Data
@ApiModel(value = "公司Dto")
public class CompanyDto implements Serializable {
    private static final long serialVersionUID = 2971301989501710708L;

    private Long id;

    /**
     * 组织编码
     */
    @ApiModelProperty(value = "组织编码")
    private String groupCode;

    /**
     * 组织名称
     */
    @ApiModelProperty(value = "组织名称")
    private String groupName;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

    /**
     * 组织类型
     */
    @ApiModelProperty(value = "组织类型")
    private String type;

    /**
     * 父ID
     */
    @ApiModelProperty(value = "父ID")
    private Long pid;

    /**
     * 联系人
     */
    @ApiModelProperty(value = "联系人")
    private String contact;

    /**
     * 组织地址
     */
    @ApiModelProperty(value = "组织地址")
    private String groupAddress;

    /**
     * 省名称
     */
    @ApiModelProperty(value = "省名称")
    private String provinceName;

    /**
     * 省编码
     */
    @ApiModelProperty(value = "省编码")
    private Long provinceId;

    /**
     * 城市名称
     */
    @ApiModelProperty(value = "城市名称")
    private String cityName;

    /**
     * 城市编码
     */
    @ApiModelProperty(value = "城市编码")
    private Long cityId;

    /**
     * 区名称
     */
    @ApiModelProperty(value = "区名称")
    private String areaName;

    /**
     * 区编码
     */
    @ApiModelProperty(value = "区编码")
    private Long areaId;

    /**
     * 街道编码
     */
    @ApiModelProperty(value = "街道编码")
    private Long streetId;

    /**
     * 街道名称
     */
    @ApiModelProperty(value = "街道名称")
    private String streetName;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址")
    private String detailAddress;

    /**
     * 联系人手机号
     */
    private String contactPhone;

    /**
     * 描述
     */
    private String remark;

    /**
     * 上级组织编码
     */
    private String parentGroupCode;

    /**
     * 上级组织名称
     */
    private String parentGroupName;

    /**
     * 四级地址数组
     */
    private List<Long> addressList;
}
