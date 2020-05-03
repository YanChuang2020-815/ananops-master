package com.ananops.provider.service.impl;

import com.alipay.api.AlipayApiException;
import com.ananops.provider.alipay.Alipay;
import com.ananops.provider.alipay.AlipayBean;
import com.ananops.provider.mapper.BmcBillMapper;
import com.ananops.provider.model.domain.BmcBill;
import com.ananops.provider.service.AlipayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bridge Wang
 * @version 1.0
 * @date 2020/4/15 19:27
 */
@Service
@Slf4j
public class AlipayServiceImpl implements AlipayService {
    @Autowired
    private Alipay alipay;

    @Resource
    private BmcBillMapper bmcBillMapper;

    @Override
    public String aliPay(AlipayBean alipayBean) throws AlipayApiException {
        return alipay.pay(alipayBean);
    }

    @Override
    public void notify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //接收参数进行校验
        Map<String, String> parameters = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            parameters.put(key, valueStr);
        }
        log.info("parameters is [parameters={}]", parameters);
        String appId = request.getParameter("app_id");//appid
        String merchantOrderNo = request.getParameter("out_trade_no");//商户订单号

        String billNo = merchantOrderNo.toLowerCase();
        if (billNo.length() <= 7){
            log.info("billNo: "+billNo+"STAGE ONE");
        }else if (billNo.startsWith("ananops")){
            billNo=billNo.substring(7);
            log.info("billNo: "+billNo+"STAGE TWO");
        }else {
            log.info("billNo: "+billNo+"STAGE THREE");
        }

        Long billNum = Long.valueOf(billNo);
        BmcBill bmcBill = bmcBillMapper.selectByPrimaryKey(billNum);
        if (bmcBill.getState().equals("已完成")){
            log.info("billNo: "+billNo+"状态为已完成，不需要修改");
        }else {
            bmcBill.setState("已完成");
            bmcBillMapper.updateByPrimaryKey(bmcBill);
            log.info("修改之后，账单信息：账单号：billNo: "+billNo+"状态信息：state:"+bmcBill.getState());
        }
        System.out.println("=======================notify===========================");
        System.out.println("===================接收到异步通知=========================");
        System.out.println("=======================notify===========================");
        response.getOutputStream().print("success");

    }
}

