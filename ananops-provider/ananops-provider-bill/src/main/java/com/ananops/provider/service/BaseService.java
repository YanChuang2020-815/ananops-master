package com.ananops.provider.service;

import com.ananops.provider.model.domain.BmcBill;
import com.ananops.provider.model.dto.BillCreateDto;
import com.ananops.provider.model.dto.BillDisplayDto;

import java.math.BigDecimal;
import java.util.List;

public interface BaseService {
    /**
     * 创建账单
     * @param billCreateDto
     * @param devicePrice
     * @param servicePrice
     * @param transactionMethod
     */
    public void insert(BillCreateDto billCreateDto, BigDecimal devicePrice, BigDecimal servicePrice, String transactionMethod);

    /**
     * 通过userId获取账单信息
     * @param userId
     * @return
     */
    public List<BillDisplayDto> getAllBillByUserId(Long userId);

    /**
     * 通过工单id获取总金额
     * @param workOrderId
     * @return
     */
    public BigDecimal getAmountByWorkOrderId(Long workOrderId);

    /**
     * 通过账单id获取账单信息
     * @param id
     * @return
     */
    public BmcBill getBillById(Long id);

    /**
     * 修改账单总金额信息
     * @param bmcBill
     * @param modifyAmount
     */
    public void modifyAmount(BmcBill bmcBill, BigDecimal modifyAmount);

    /**
     * 通过userId和目前状态查询所有账单列表
     * @param userId
     * @param state
     * @return
     */
    public List<BmcBill> getAllUBillByState(Long userId, String state);

    /**
     * 通过工单id查询所有账单列表
     * @param workOrderId
     * @return
     */
    List<BmcBill> getBillByWorkOrderId(Long workOrderId);
}


