package com.ananops.provider.web;

import com.alipay.api.AlipayApiException;
import com.ananops.provider.alipay.AlipayBean;
import com.ananops.provider.mapper.BmcBillMapper;
import com.ananops.provider.model.domain.BmcBill;
import com.ananops.provider.service.AlipayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Bridge Wang
 * @version 1.0
 * @date 2020/4/15 19:26
 */
@RestController()
@RequestMapping("order")
@Slf4j
public class AlipayController {
    @Autowired
    private AlipayService alipayService;

    @Resource
    private BmcBillMapper bmcBillMapper;

    /**
     * 阿里支付
     * @param outTradeNo
     * @param subject
     * @param totalAmount
     * @param body
     * @return
     * @throws AlipayApiException
     */
    @PostMapping(value = "alipay")
    public String alipay(String outTradeNo, String subject, String totalAmount, String body) throws AlipayApiException {
        AlipayBean alipayBean = new AlipayBean();
        alipayBean.setOut_trade_no(outTradeNo);
        alipayBean.setSubject(subject);
        alipayBean.setTotal_amount(totalAmount);
        alipayBean.setBody(body);
        return alipayService.aliPay(alipayBean);
    }
    @GetMapping(value = "return")
    public String success(String charset,
                          String out_trade_no,
                          String method,
                          String total_amount,
                          String sign,
                          String trade_no,
                          String auth_app_id,
                          String version,
                          String app_id,
                          String sign_type,
                          String seller_id,
                          String timestamp){
        log.info("***********************");
        log.info("charset: "+charset);
        log.info("out_trade_no: "+out_trade_no);
        log.info("method: "+method);
        log.info("total_amount: "+total_amount);
        log.info("sign: "+sign);
        log.info("trade_no: "+trade_no);
        log.info("auth_app_id: "+auth_app_id);
        log.info("version: "+version);
        log.info("app_id: "+app_id);
        log.info("sign_type: "+sign_type);
        log.info("seller_id: "+seller_id);
        log.info("timestamp: "+timestamp);
        String billNo = out_trade_no.toLowerCase();
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
        return "已成功完成支付";
    }
    @PostMapping(value = "notify")
    public Object notifyUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        alipayService.notify(request,response);
        System.out.println("===================notify========================");
        System.out.println("================接收到异步通知=====================");
        System.out.println("===================notify========================");
        return "success";
    }
}
