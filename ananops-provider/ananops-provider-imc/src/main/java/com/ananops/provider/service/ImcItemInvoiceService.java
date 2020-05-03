package com.ananops.provider.service;

import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.IService;
import com.ananops.provider.model.domain.ImcItemInvoice;
import com.ananops.provider.model.dto.FormDataDto;
import com.ananops.provider.model.dto.ImcInvoiceQueryDto;
import com.ananops.provider.model.dto.oss.ElementImgUrlDto;
import com.ananops.provider.model.dto.oss.OptUploadFileRespDto;

import java.util.List;

/**
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020/4/21 下午8:29
 */

public interface ImcItemInvoiceService extends IService<ImcItemInvoice> {

    /**
     * 查询巡检单列表
     *
     * @param imcInvoiceQueryDto
     *
     * @param loginAuthDto
     *
     * @return
     */
    List<ImcItemInvoice> queryInvoiceList(ImcInvoiceQueryDto imcInvoiceQueryDto, LoginAuthDto loginAuthDto);

    /**
     * 根据InvoiceId查询表单项详情
     *
     * @param invoiceId
     *
     * @return
     */
    FormDataDto queryDetailsById(Long invoiceId);

    /**
     * 保存工程师提交的巡检单数据
     *
     * @param formDataDto 巡检单数据
     *
     * @param loginAuthDto 登录用户信息
     *
     * @return
     */
    Integer saveData(FormDataDto formDataDto, LoginAuthDto loginAuthDto);

    /**
     * 查看已完成的巡检单预览文件
     *
     * @param itemId 巡检任务子项ID
     *
     * @param loginAuthDto 登录用户信息
     *
     * @return
     */
    List<ElementImgUrlDto> getInvoicePreview(Long itemId, LoginAuthDto loginAuthDto);

    /**
     * 用户确认后，将用户名称填入到巡检单据的用户确认字段中
     *
     * @param id
     *
     * @param loginAuthDto
     */
    void handleUserConfirm(Long itemId, LoginAuthDto loginAuthDto);
}
