package com.ananops.provider.service.hystrix;

import com.ananops.provider.service.UfloFeignApi;
import com.ananops.provider.service.dto.ApproSubmitDto;
import com.ananops.wrapper.Wrapper;
import org.springframework.stereotype.Component;

@Component
public class UfloFeignHystrix implements UfloFeignApi {

    @Override
    public Wrapper<String> submit(ApproSubmitDto approSubmitDto) {
      return null;
    }
}
