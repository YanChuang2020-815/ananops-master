package com.ananops.provider.service;

import com.ananops.provider.model.dto.SysDictItemsDto;
import com.ananops.provider.service.hystrix.MdcDictItemFeignHystrix;
import com.ananops.security.feign.OAuth2FeignAutoConfiguration;
import com.ananops.wrapper.Wrapper;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020-04-07 16:17
 */
@FeignClient(value = "ananops-provider-mdc", configuration = OAuth2FeignAutoConfiguration.class, fallback = MdcDictItemFeignHystrix.class)
public interface MdcDictItemFeignApi {

    @PostMapping(value = "/api/dictItem/getSysDictItems")
    Wrapper<SysDictItemsDto> getSysDictItems(@RequestParam("userId") Long userId);
}
