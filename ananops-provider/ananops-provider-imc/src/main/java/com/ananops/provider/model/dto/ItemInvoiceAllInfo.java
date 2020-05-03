package com.ananops.provider.model.dto;

import com.ananops.provider.model.domain.ImcInspectionItem;
import com.ananops.provider.model.domain.ImcItemInvoice;
import com.ananops.provider.model.domain.ImcItemInvoiceDesc;
import com.ananops.provider.model.domain.ImcItemInvoiceDevice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 包装巡检单据所有关联数据
 *
 * @author Bingyue Duan
 *
 * @version 1.0
 *
 * @date 2020/4/23 上午9:42
 */
@Data
@ApiModel
public class ItemInvoiceAllInfo implements Serializable {

    private static final long serialVersionUID = 4078474476424790487L;

    /**
     * 巡检子项数据
     */
    @ApiModelProperty(value = "巡检子项数据")
    private ImcInspectionItem imcInspectionItem;

    /**
     * 巡检单据数据
     */
    @ApiModelProperty(value = "巡检任务子项对应的状态")
    private ImcItemInvoice invoice;

    /**
     * 资产描述列表
     */
    @ApiModelProperty(value = "资产描述列表")
    private List<ImcItemInvoiceDevice> assetList;

    /**
     * 常规巡检详情
     */
    @ApiModelProperty(value = "常规巡检详情")
    private List<ImcItemInvoiceDesc> inspcDetailList;
}
