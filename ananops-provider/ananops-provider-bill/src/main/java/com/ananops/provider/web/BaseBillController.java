package com.ananops.provider.web;

import com.ananops.core.support.BaseController;
import com.ananops.provider.model.domain.BmcBill;
import com.ananops.provider.model.dto.*;
import com.ananops.provider.model.dto.PmcContractDto;
import com.ananops.provider.model.dto.PmcProjectDto;
import com.ananops.provider.service.PmcContractFeignApi;
import com.ananops.provider.service.PmcProjectFeignApi;
import com.ananops.provider.service.impl.BaseServiceImpl;
import com.ananops.provider.utils.WrapMapper;
import com.ananops.provider.utils.Wrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/bill", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "WEB - BaseBillController", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class BaseBillController extends BaseController {

//    @Resource
//    RdcDeviceOrderFeignApi rdcDeviceOrderFeignApi;
    @Resource
    PmcContractFeignApi pmcContractFeignApi;
    @Resource
    PmcProjectFeignApi pmcProjectFeignApi;
    @Resource
    BaseServiceImpl baseServiceImpl;

    /**
     * 创建账单
     * @param billCreateDto
     * @return
     */
    @PostMapping(value = "/create")
    @ApiOperation(httpMethod = "POST",value = "创建账单")
    public Wrapper<String> createNew(@ApiParam(name = "body",value="账单信息") @RequestBody BillCreateDto billCreateDto){
        // 判断projectId是否为空
        if (billCreateDto.getProjectId() == null){
            return WrapMapper.error("创建工单时传入的参数projectId为空！getProjectId = "+billCreateDto.getProjectId());
        }
        // 通过projectId找到project，再通过project信息找到ContractId，然后找到contract信息
        PmcProjectDto pmcProjectDto = pmcProjectFeignApi.getProjectByProjectId(billCreateDto.getProjectId()).getResult();
        if (pmcProjectDto == null){
            return WrapMapper.error("getProjectByProjectId获得的项目信息为空！projectId = "+billCreateDto.getProjectId());
        }
        if (pmcProjectDto.getContractId() == null){
            return WrapMapper.error("pmcProjectDto中的合同id为空！pmcProjectDto.getContractId() = "+pmcProjectDto.getContractId());
        }
        PmcContractDto pmcContractDto = pmcContractFeignApi.getContractById(pmcProjectDto.getContractId()).getResult();
        if (pmcContractDto == null){
            return WrapMapper.error("通过getContractById获得的合同信息为空！ContractId = "+pmcProjectDto.getContractId());
        }
        BigDecimal servicePrice = pmcContractDto.getPaymentMoney();
        String transactionMethod = pmcContractDto.getPaymentType().toString();

        if (billCreateDto.getDeviceDtos() != null){
            List<DeviceDto> deviceDtoList = billCreateDto.getDeviceDtos();
            List<Long> deviceIds = new ArrayList<>();
            for (DeviceDto deviceDto : deviceDtoList) {
                deviceIds.add(deviceDto.getDeviceId());
            }
        }
//        com.ananops.wrapper.Wrapper<Object> objects = rdcDeviceOrderFeignApi.getPriceOfDevices(deviceIds);
//        List<JSONObject> jsonObjects = (List<JSONObject>)objects.getResult();
        BigDecimal devicePrice = BigDecimal.valueOf(0);
//        for (JSONObject object : jsonObjects) {
//            devicePrice += object.getBigDecimal("price").floatValue();
//        }

        baseServiceImpl.insert(billCreateDto, devicePrice, servicePrice, transactionMethod);
        return  WrapMapper.ok("success");
    }

    /**
     * 用于实时获取创建账单前展示给用户的总金额
     * @param billCreateDto
     * @return
     */
    @PostMapping(value = "/createFakeOrder")
    @ApiOperation(httpMethod = "POST",value = "创建账单")
    public Wrapper<BigDecimal> createFakeNew(@ApiParam(name = "body",value="账单信息") @RequestBody BillCreateDto billCreateDto){
        logger.info("createFakeNew - 创建账单. billCreateDto={}", billCreateDto);
        // 判断projectId是否为空
        if (billCreateDto.getProjectId() == null){
            return WrapMapper.error("创建工单时传入的参数projectId为空！getProjectId = "+billCreateDto.getProjectId());
        }
        // 通过projectId找到project，再通过project信息找到ContractId，然后找到contract信息
        Long contractId = getContractId(billCreateDto.getProjectId());
//        if (pmcProjectDto == null){
//            return WrapMapper.error("getProjectByProjectId获得的项目信息为空！projectId = "+billCreateDto.getProjectId());
//        }
//        if (pmcProjectDto.getContractId() == null){
//            return WrapMapper.error("pmcProjectDto中的合同id为空！pmcProjectDto.getContractId() = "+pmcProjectDto.getContractId());
//        }
//        PmcContractDto pmcContractDto = pmcContractFeignApi.getContractById(contractId).getResult();
//        if (pmcContractDto == null){
//            return WrapMapper.error("通过getContractById获得的合同信息为空！ContractId = "+contractId);
//        }

        BigDecimal servicePrice = getServicePrice(contractId);
//        List<DeviceDto> deviceDtoList = billCreateDto.getDeviceDtos();
//        List<Long> deviceIds = new ArrayList<>();
//        for (DeviceDto deviceDto : deviceDtoList) {
//            deviceIds.add(deviceDto.getDeviceId());
//        }
//        com.ananops.wrapper.Wrapper<Object> objects = rdcDeviceOrderFeignApi.getPriceOfDevices(deviceIds);
//        List<JSONObject> jsonObjects = (List<JSONObject>)objects.getResult();
        BigDecimal devicePrice = BigDecimal.valueOf(0);
//        for (JSONObject object : jsonObjects) {
//            devicePrice += object.getBigDecimal("price").floatValue();
//        }
        if (servicePrice != null) {
            return WrapMapper.ok(servicePrice.add(devicePrice));
        }
        return WrapMapper.ok(devicePrice);
    }

    /**
     * 一个小方法，用于获取合同ID
     * @param projectId 项目ID
     * @return 合同ID
     */
    public Long getContractId(Long projectId){
        return pmcProjectFeignApi.getProjectByProjectId(projectId).getResult().getContractId();
    }

    /**
     * 一个小方法，用于获取服务金额
     * @param contractId 合同ID
     * @return 服务金额
     */
    public BigDecimal getServicePrice(Long contractId){
        return pmcContractFeignApi.getContractById(contractId).getResult().getPaymentMoney();
    }


    /**
     * 用于获取统计信息
     * @param userId 用户ID
     * @param amount 金额
     * @param year 年
     * @param month 月
     * @param length 时间跨度（月）
     * @return 最终计算得到的统计信息
     */
    @GetMapping(value = "/getStatistics/{userId}/{amount}/{year}/{month}/{length}")
    @ApiOperation(httpMethod = "GET",value = "获取统计信息")
    public Wrapper<BillStatistics> getStatistics(@ApiParam(name = "userId", value = "用户ID") @RequestParam Long userId,
                                                 @ApiParam(name = "amount", value = "金额") @RequestParam BigDecimal amount,
                                                 @ApiParam(name = "year", value = "年") @RequestParam int year,
                                                 @ApiParam(name = "month", value = "月") @RequestParam int month,
                                                 @ApiParam(name = "length", value = "时间跨度（月）") @RequestParam int length){
        BillStatistics billStatistics = new BillStatistics();
        billStatistics.setBillNum(baseServiceImpl.getBillNumByUserId(userId));
        billStatistics.setBillNumByUserIdAndAmount(baseServiceImpl.getBillNumByUserIdAndAmount(userId, amount));
        billStatistics.setBillNumByUserIdAndStateCreating(baseServiceImpl.getBillNumByUserIdAndState(userId, "创建中"));
        billStatistics.setBillNumByUserIdAndStateFinished(baseServiceImpl.getBillNumByUserIdAndState(userId, "已完成"));
        billStatistics.setBillNumByUserIdAndStateNowPay(baseServiceImpl.getBillNumByUserIdAndTransactionMethod(userId, "现结"));
        billStatistics.setBillNumByUserIdAndStatePeriodPay(baseServiceImpl.getBillNumByUserIdAndTransactionMethod(userId, "账期"));
        billStatistics.setBillNumByUserIdAndStateYearPay(baseServiceImpl.getBillNumByUserIdAndTransactionMethod(userId, "年结"));
        billStatistics.setMoneySum(baseServiceImpl.getMoneySumByUserIdYearMonthAndLength(userId, year, month, length));
        return WrapMapper.ok(billStatistics);
    }

    /**
     * 根据用户ID获取该用户下所产生的所有账单
     * @param userId 用户ID
     * @return 一个包含账单所有信息的LIST
     */
    @GetMapping(value = "/getAllByUser/{userId}")
    @ApiOperation(httpMethod = "GET",value = "根据用户id获取所有账单")
    public  Wrapper<List<BillDisplayDto>> getAllBillByUserId(@ApiParam(name = "userId",value = "用户id") @PathVariable Long userId){
        return WrapMapper.ok(baseServiceImpl.getAllBillByUserId(userId));
    }

    /**
     * 用于通过用户ID和账单状态获取该用户下的所有已完成账单
     * @param userId 用户ID
     * @param state 账单状态
     * @return 包含已完成账单所有信息的LIST
     */
    @GetMapping(value="/getBillsByUserIdAndState/{userId}/{state}")
    @ApiOperation(httpMethod = "GET",value = "根据用户id和账单状态获取所有已完成账单")
    public Wrapper<List<BillDisplayDto>> getBillsByUserIdAndState(@ApiParam(name = "userId",value = "用户id") @RequestParam Long userId,
                                                         @ApiParam(name = "state",value = "账单状态") @RequestParam String state){
        return WrapMapper.ok(baseServiceImpl.getBillsByUserIdAndState(userId,state));
    }

    @GetMapping(value="/getMoneySumByUserIdYearMonthAndLength/{userId}/{year}/{month}/{length}")
    @ApiOperation(httpMethod = "GET",value = "根据用户ID、年、月份以及时间跨度来获取账单的统计金额")
    public Wrapper<Object> getMoneySumByUserIdMonthAndLength(@ApiParam(name="userId",value="用户id") @RequestParam Long userId,
                                                  @ApiParam(name="year",value="年份") @RequestParam int year,
                                                  @ApiParam(name="month",value="月份") @RequestParam int month,
                                                  @ApiParam(name="length",value="时间跨度") @RequestParam int length){
        return WrapMapper.ok(baseServiceImpl.getMoneySumByUserIdYearMonthAndLength(userId, year, month, length));
    }

    @GetMapping(value="/getMoneySumByUserIdYearAndMonth/{userId}/{year}/{month}")
    @ApiOperation(httpMethod = "GET",value = "根据用户ID、年、月份来获取账单的统计金额")
    public Wrapper<BigDecimal> getMoneySumByUserIdAndMonth(@ApiParam(name="userId",value="用户id") @RequestParam Long userId,
                                                             @ApiParam(name="year",value="年份") @RequestParam int year,
                                                             @ApiParam(name="month",value="月份") @RequestParam int month){
        return WrapMapper.ok(baseServiceImpl.getMoneySumByUserIdYearAndMonth(userId, year, month));
    }

    @GetMapping(value = "/getBillNumByUserId/{userId}")
    @ApiOperation(httpMethod = "GET",value = "根据用户id获取账单数量")
    public Wrapper<Integer> getBillNumByUserId(@ApiParam(name = "userId",value = "用户id") @RequestParam Long userId){
        return WrapMapper.ok(baseServiceImpl.getBillNumByUserId(userId));
    }

    @GetMapping(value = "/getBillNumByUserIdAndState/{userId}/{state}")
    @ApiOperation(httpMethod = "GET",value = "根据用户id和支付状态获取账单数量")
    public Wrapper<Integer> getBillNumByUserIdAndState(@ApiParam(name = "userId",value = "用户id") @RequestParam Long userId,
                                                     @ApiParam(name = "state",value = "状态") @RequestParam String state){
        return WrapMapper.ok(baseServiceImpl.getBillNumByUserIdAndState(userId, state));
    }

    @GetMapping(value = "/getBillNumByUserIdAndTransactionMethod/{userId}/{transactionMethod}")
    @ApiOperation(httpMethod = "GET",value = "根据用户id和交易方式获取帐单数量")
    public Wrapper<Integer> getBillNumByUserIdAndTransactionMethod(@ApiParam(name = "userId",value = "用户id") @RequestParam Long userId,
                                                       @ApiParam(name = "transactionMethod",value = "交易方式") @RequestParam String transactionMethod){
        return WrapMapper.ok(baseServiceImpl.getBillNumByUserIdAndTransactionMethod(userId, transactionMethod));
    }

    @GetMapping(value = "/getBillNumByUserIdAndAmount/{userId}/{amount}")
    @ApiOperation(httpMethod = "GET",value = "根据用户id和预约金额获取账单数量")
    public Wrapper<Integer> getBillNumByUserIdAndAmount(@ApiParam(name = "userId",value = "用户id") @RequestParam Long userId,
                                                                   @ApiParam(name = "amount",value = "交易方式") @RequestParam BigDecimal amount){
        return WrapMapper.ok(baseServiceImpl.getBillNumByUserIdAndAmount(userId, amount));
    }

    @PostMapping(value = "/getBillById/{id}")
    @ApiOperation(httpMethod = "POST",value = "根据账单id获取账单")
    public  Wrapper<BillDisplayDto> getBillById(@PathVariable Long id){
        return WrapMapper.ok(baseServiceImpl.getOneBillById(id));
    }

    @GetMapping(value = "/getAmountByWorkOrder/{workOrderId}")
    @ApiOperation(httpMethod = "GET",value = "根据工单id获取金额")
    public Wrapper<BigDecimal> getAmountByWorkOrderId(@ApiParam(name = "workOrderId",value = "工单id") @RequestParam Long workOrderId){
        return WrapMapper.ok(baseServiceImpl.getAmountByWorkOrderId(workOrderId));
    }

    @PutMapping(value = "/modifyAmount")
    @ApiOperation(httpMethod = "PUT",value = "修改金额")
    public void modifyAmount(@ApiParam(name = "modifyAmount",value = "修改金额") @RequestBody BillModifyAmountDto billModifyAmountDto){
        BmcBill bmcBill = baseServiceImpl.getBillById(billModifyAmountDto.getId());
        baseServiceImpl.modifyAmount(bmcBill, billModifyAmountDto.getModifyAmount());
    }

    @GetMapping(value = "/getUBill/{userId}/{state}")
    @ApiOperation(httpMethod = "GET",value = "根据状态及用户id获取账单")
    public Wrapper<List<BmcBill>> getAllUBillByState(@ApiParam(name = "userId",value = "用户id") @RequestParam Long userId,
                                                      @ApiParam(name = "state",value = "状态") @RequestParam String state){
        return WrapMapper.ok(baseServiceImpl.getAllUBillByState(userId, state));
    }

    @GetMapping(value = "/getBillByWorkOrderId/{workOrderId}")
    @ApiOperation(httpMethod = "GET",value = "根据状态及用户id获取账单")
    public Wrapper<List<BmcBill>> getBillByWorkOrderId(@ApiParam(name = "workOrderId",value = "工单id") @RequestParam Long workOrderId){
        return WrapMapper.ok(baseServiceImpl.getBillByWorkOrderId(workOrderId));
    }
}
