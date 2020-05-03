package com.ananops.provider.web.rpc;

import com.ananops.core.support.BaseController;
import com.ananops.provider.mapper.BmcBillMapper;
import com.ananops.provider.model.domain.BmcBill;
import com.ananops.provider.service.BillFeignApi;
import com.ananops.wrapper.WrapMapper;
import com.ananops.wrapper.Wrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@RestController
@Api(value = "API - BillFeignClient", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class BillFeignClient extends BaseController implements BillFeignApi {
    @Autowired
    BmcBillMapper bmcBillMapper;

    @Override
    @ApiOperation(httpMethod = "GET", value = "根据工单ID查询金额")

    public Wrapper<BigDecimal> getAmountByWorkOrderId(Long workOrderId) {
        List<BmcBill> list=new ArrayList<>();
        Example example = new Example(BmcBill.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(String.valueOf(workOrderId));
        list= bmcBillMapper.selectByExample(example);
        BmcBill bmcBill=new BmcBill();
        if(list!=null&&list.size()>0){
            for (BmcBill bmcBill1:list){
                bmcBill=bmcBill1;
            }
        }
        return WrapMapper.ok(bmcBill.getAmount());
    }
}
