package com.ananops.provider.service;

import com.alipay.api.AlipayApiException;
import com.ananops.provider.alipay.AlipayBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Bridge Wang
 * @version 1.0
 * @date 2020/4/15 19:26
 */
public interface AlipayService {
    /**
     * 支付宝支付接口
     * @param alipayBean
     * @return
     * @throws AlipayApiException
     */
    String aliPay(AlipayBean alipayBean) throws AlipayApiException;

    /**
     * 付款异步通知调用地址
     * @param request 新增参数
     * @return 新增返回值
     */
    void notify(HttpServletRequest request, HttpServletResponse response) throws IOException;
}

