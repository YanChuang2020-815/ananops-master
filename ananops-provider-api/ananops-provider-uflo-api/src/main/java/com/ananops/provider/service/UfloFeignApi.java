package com.ananops.provider.service;

import com.ananops.provider.service.dto.ApproSubmitDto;
import com.ananops.provider.service.hystrix.UfloFeignHystrix;
import com.ananops.security.feign.OAuth2FeignAutoConfiguration;
import com.ananops.wrapper.Wrapper;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(value = "ananops-provider-uflo",configuration = OAuth2FeignAutoConfiguration.class,fallback = UfloFeignHystrix.class)
public interface UfloFeignApi {

    @PostMapping(value = "/api/uflo/submit")
    Wrapper<String> submit(@RequestBody ApproSubmitDto approSubmitDto);
}
