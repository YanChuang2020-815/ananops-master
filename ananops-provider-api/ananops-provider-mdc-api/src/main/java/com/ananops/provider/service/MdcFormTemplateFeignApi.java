package com.ananops.provider.service;

import com.ananops.provider.model.dto.FormTemplateDto;
import com.ananops.provider.service.hystrix.MdcFormTemplateFeignHystrix;
import com.ananops.security.feign.OAuth2FeignAutoConfiguration;
import com.ananops.wrapper.Wrapper;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020/4/21 下午3:25
 */
@FeignClient(value = "ananops-provider-mdc", configuration = OAuth2FeignAutoConfiguration.class, fallback = MdcFormTemplateFeignHystrix.class)
public interface MdcFormTemplateFeignApi {

    @PostMapping(value = "/api/formTemplate/getFormTemplateByProjectId")
    Wrapper<FormTemplateDto> getFormTemplateByProjectId(@RequestParam("projectId") Long projectId);
}
