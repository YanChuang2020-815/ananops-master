package com.ananops.provider.service.hystrix;

import com.ananops.provider.model.dto.FormTemplateDto;
import com.ananops.provider.service.MdcFormTemplateFeignApi;
import com.ananops.wrapper.Wrapper;
import org.springframework.stereotype.Component;

/**
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020/4/21 下午3:26
 */
@Component
public class MdcFormTemplateFeignHystrix implements MdcFormTemplateFeignApi {

    @Override
    public Wrapper<FormTemplateDto> getFormTemplateByProjectId(Long projectId) {
        return null;
    }
}
