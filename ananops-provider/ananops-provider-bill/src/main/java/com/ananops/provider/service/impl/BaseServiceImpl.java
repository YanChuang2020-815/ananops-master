package com.ananops.provider.service.impl;

import com.alibaba.fastjson.JSON;
import com.ananops.provider.mapper.BmcBillMapper;
import com.ananops.provider.model.domain.BmcBill;
import com.ananops.provider.model.dto.BillCreateDto;
import com.ananops.provider.model.dto.BillDisplayDto;
import com.ananops.provider.model.dto.group.CompanyDto;
import com.ananops.provider.model.dto.user.UserInfoDto;
import com.ananops.provider.model.service.UacGroupBindUserFeignApi;
import com.ananops.provider.model.service.UacGroupFeignApi;
import com.ananops.provider.model.service.UacUserFeignApi;
import com.ananops.provider.model.vo.CompanyVo;
import com.ananops.provider.service.BaseService;
import com.ananops.provider.service.SpcCompanyFeignApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class BaseServiceImpl implements BaseService {

    @Autowired
    BmcBillMapper bmcBillMapper;

    @Resource
    private UacUserFeignApi uacUserFeignApi;

    @Resource
    private UacGroupFeignApi uacGroupFeignApi;

    @Resource
    private UacGroupBindUserFeignApi uacGroupBindUserFeignApi;

    @Resource
    private SpcCompanyFeignApi spcCompanyFeignApi;

    @Override
    public void insert(BillCreateDto billCreateDto, BigDecimal devicePrice, BigDecimal servicePrice, String transactionMethod) {
//        JSONObject jsonObject=JSONObject.parseObject(billCreateDto.toString());
//        String paymentMethod=jsonObject.get("paymentMethod").toString();
//        String userid=jsonObject.get("userid").toString();
//        String workorderid=jsonObject.get("workorderid").toString();
//        String supplier=jsonObject.get("supplier").toString();
        //TODO
//        Float amount=Float.valueOf(jsonObject.get("amount").toString());
//        String transactionMethod=jsonObject.get("transactionMethod").toString();
//        String payDto = jsonObject.get("payDto").toString();
//        JsonObject jsonObject1 = new JsonParser().parse(payDto).getAsJsonObject();
        Date date=new Date();
        Long time=date.getTime();
        BmcBill bill = new BmcBill();

        int billIndex = bmcBillMapper.selectAll().size()%100000;

        String idString = String.format("%04d", date.getYear()+1900)+String.format("%02d", date.getMonth()+1)+String.format("%02d",date.getDate())+String.format("%02d", date.getHours())+String.format("%02d", date.getMinutes())+String.format("%02d", date.getSeconds())+String.format("%05d",billIndex);
        Long id = Long.valueOf(idString);
        bill.setId(id);
        bill.setPaymentMethod(billCreateDto.getPaymentMethod());
        /*
        transactionMethod（1-现结、2-账期、3-年结）
         */
        switch (transactionMethod){
            case "1":
                bill.setTransactionMethod("现结");
                break;
            case "2":
                bill.setTransactionMethod("账期");
                break;
            case "3":
                bill.setTransactionMethod("年结");
                break;
        }
        bill.setUserId(billCreateDto.getUserId());
        bill.setTime(time);
        Long groupId = uacGroupBindUserFeignApi.getCompanyGroupIdByUserId(billCreateDto.getSupplier()).getResult();
        CompanyDto companyDto = uacGroupFeignApi.getCompanyInfoById(groupId).getResult();
        switch (companyDto.getType()){
            case "department":
                groupId = companyDto.getPid();
                break;
            case "company":
                break;
            default:
                log.info("暂不支持此类公司类型，公司类型："+companyDto.getType());
        }
        bill.setSupplier(groupId);
        bill.setWorkOrderId(billCreateDto.getWorkOrderId());
        bill.setProjectId(billCreateDto.getProjectId());
        bill.setState(billCreateDto.getState());
        bill.setDeviceAmount(devicePrice);
        bill.setServiceAmount(servicePrice);
        bill.setAmount(bill.getDeviceAmount().add(bill.getServiceAmount()));
        UserInfoDto uacUserInfo = uacUserFeignApi.getUacUserById(bill.getUserId()).getResult();
        if(uacUserInfo != null){
            bill.setUserName(uacUserInfo.getUserName());
        }else{
            log.info("通过用户ID："+bill.getUserId()+"找到的用户名为空");
        }
        CompanyVo companyVo = spcCompanyFeignApi.getCompanyDetailsById(groupId).getResult();
        if(companyVo != null){
            bill.setSupplierName(companyVo.getGroupName());
        }else{
            log.info("通过服务商ID："+bill.getSupplier()+"找到的服务商名为空");
        }
        bill.setVersion(1);
        bill.setCreator(bill.getUserName());
        bill.setCreatorId(bill.getUserId());
        bill.setCreatedTime(new Date());
        bill.setLastOperator(bill.getUserName());
        bill.setLastOperatorId(bill.getUserId());
        bill.setUpdateTime(new Date());
        bmcBillMapper.insertSelective(bill);
    }

    @Override
    public List<BillDisplayDto> getAllBillByUserId(Long userId) {
        //使用模板example获取BaseBill类
        Example example = new Example(BmcBill.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);

        //将获取到的BaseBill类添加到basebills列表中去
//        List<TaskDto> imcTaskDtos = imcTaskFeignApi.getByFacilitatorId(taskQueryDto).getResult();
        List<BmcBill> bmcBills = bmcBillMapper.selectByExample(example);

        //创建待返回的账单展示传输对象
        List<BillDisplayDto> billDisplayDtoList = new ArrayList<>();
        if(bmcBills != null){
            for(BmcBill bmcBill : bmcBills){
                BillDisplayDto billDisplayDto = new BillDisplayDto();
                try{
                    BeanUtils.copyProperties(bmcBill,billDisplayDto);
                }catch (Exception e){
                    log.error("账单BaseBill与账单展示billDisplayDto属性拷贝异常");
                    e.printStackTrace();
                }

                //没问题就添加到待返回列表中
                billDisplayDtoList.add(billDisplayDto);

            }
        }
        billDisplayDtoList.sort(BillDisplayDto.Comparators.TIME);
        return billDisplayDtoList;
    }

    public List<BillDisplayDto> getBillsByUserIdAndState(Long userId, String state){
        Example example = new Example(BmcBill.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        List<BmcBill> bmcBills = bmcBillMapper.selectByExample(example);
        List<BillDisplayDto> billDisplayDtoList = new ArrayList<>();
        if(bmcBills != null){
            for(BmcBill bmcBill : bmcBills){
                BillDisplayDto billDisplayDto = new BillDisplayDto();
                if (!bmcBill.getState().equals(state)){
                    continue;
                }
                try{
                    BeanUtils.copyProperties(bmcBill,billDisplayDto);
                }catch (Exception e){
                    log.error("账单BaseBill与账单展示billDisplayDto属性拷贝异常");
                    e.printStackTrace();
                }
                billDisplayDtoList.add(billDisplayDto);

            }
        }
        billDisplayDtoList.sort(BillDisplayDto.Comparators.TIME);
        return billDisplayDtoList;
    }

    public Object getMoneySumByUserIdYearMonthAndLength(Long userId, int year, int month, int length){
        Example example = new Example(BmcBill.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        List<BmcBill> bmcBills = bmcBillMapper.selectByExample(example);
        List<BigDecimal> moneySums = new ArrayList<>();
        if (bmcBills == null){
            log.error("bmcBills list is null!");
        }
        assert bmcBills != null;
        int calcYear = year;
        int calcMonth = month;
        for (int i = 0;i<length;i++){
            BigDecimal moneySum = new BigDecimal(0);
            for (BmcBill bmcBill:bmcBills){
                Date billDate = new Date();
                billDate.setTime(bmcBill.getTime());
                int billYear = billDate.getYear()+1900;
                int billMonth = billDate.getMonth()+1;
                if (billYear == calcYear && billMonth == calcMonth){
                    moneySum = moneySum.add(bmcBill.getAmount());
                }
            }
            calcMonth--;
            if (calcMonth == 0){
                calcMonth = 12;
                calcYear--;
            }
            moneySums.add(moneySum);
        }
        Collections.reverse(moneySums);
        return JSON.toJSON(moneySums);
    }

    public BigDecimal getMoneySumByUserIdYearAndMonth(Long userId, int year, int month){
        Example example = new Example(BmcBill.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        List<BmcBill> bmcBills = bmcBillMapper.selectByExample(example);
        if (bmcBills == null){
            log.error("bmcBills list is null!");
        }
        assert bmcBills != null;
        BigDecimal moneySum = new BigDecimal(0);
        for (BmcBill bmcBill:bmcBills){
            Date billDate = new Date();
            billDate.setTime(bmcBill.getTime());
            int billYear = billDate.getYear()+1900;
            int billMonth = billDate.getMonth()+1;
            if (billYear == year && billMonth == month){
                moneySum = moneySum.add(bmcBill.getAmount());
            }
        }
        return moneySum;
    }

    public BillDisplayDto getOneBillById(Long id) {
        BmcBill bmcBill = getBillById(id);

        //创建待返回的账单展示传输对象
        BillDisplayDto billDisplayDto = new BillDisplayDto();

        try{
            BeanUtils.copyProperties(bmcBill,billDisplayDto);
        }catch (Exception e){
            log.error("账单BaseBill与账单展示billDisplayDto属性拷贝异常");
            e.printStackTrace();
        }

        return billDisplayDto;
    }

    public int getBillNumByUserId(Long userId){
        Example example = new Example(BmcBill.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);

        return bmcBillMapper.selectCountByExample(example);
    }

    public int getBillNumByUserIdAndState(Long userId, String state) {
        Example example = new Example(BmcBill.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        List<BmcBill> list = bmcBillMapper.selectByExample(example);
        List<BmcBill> bmcBills = new ArrayList<>();
        if(list != null && list.size()>0){
            for(BmcBill bmcBill:list){
                if(bmcBill.getState().equals(state)){
                    bmcBills.add(bmcBill);
                }
            }
        }
        return bmcBills.size();
    }

    public int getBillNumByUserIdAndTransactionMethod(Long userId, String transactionMethod) {
        Example example = new Example(BmcBill.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        List<BmcBill> list = bmcBillMapper.selectByExample(example);
        List<BmcBill> bmcBills = new ArrayList<>();
        if(list != null && list.size()>0){
            for(BmcBill bmcBill:list){
                if(bmcBill.getTransactionMethod().equals(transactionMethod)){
                    bmcBills.add(bmcBill);
                }
            }
        }
        return bmcBills.size();
    }

    public int getBillNumByUserIdAndAmount(Long userId, BigDecimal amount) {
        Example example = new Example(BmcBill.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        List<BmcBill> list = bmcBillMapper.selectByExample(example);
        List<BmcBill> bmcBills = new ArrayList<>();
        if(list != null && list.size()>0){
            for(BmcBill bmcBill:list){
                if(bmcBill.getAmount().compareTo(amount) > 0){
                    bmcBills.add(bmcBill);
                }
            }
        }
        return bmcBills.size();
    }

    @Override
    public BigDecimal getAmountByWorkOrderId(Long workOrderId) {
        Example example = new Example(BmcBill.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("workOrderId",workOrderId);
        List<BmcBill> list = bmcBillMapper.selectByExample(example);
        BmcBill bmcBill = list.get(0);
        return bmcBill.getAmount();
    }

    @Override
    public BmcBill getBillById(Long id) {
        return bmcBillMapper.selectByPrimaryKey(id);
    }

    @Override
    public void modifyAmount(BmcBill bmcBill, BigDecimal modifyAmount) {
        bmcBill.setAmount(modifyAmount);
        bmcBillMapper.updateByPrimaryKey(bmcBill);
    }

    @Override
    public List<BmcBill> getAllUBillByState(Long userId, String state) {
        Example example = new Example(BmcBill.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        List<BmcBill> list = bmcBillMapper.selectByExample(example);
        List<BmcBill> bmcBills = new ArrayList<>();
        if(list != null && list.size()>0){
            for(BmcBill bmcBill:list){
                if(bmcBill.getState().equals(state)){
                    bmcBills.add(bmcBill);
                }
            }
        }
        return bmcBills;
    }

    @Override
    public List<BmcBill> getBillByWorkOrderId(Long workOrderId) {
        Example example = new Example(BmcBill.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(workOrderId);
        return bmcBillMapper.selectByExample(example);
    }
}
