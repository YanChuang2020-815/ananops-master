package com.ananops.provider.web.rpc;

import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.base.exception.BusinessException;
import com.ananops.provider.mapper.MdcFormTemplateItemMapper;
import com.ananops.provider.mapper.MdcFormTemplateMapper;
import com.ananops.provider.model.domain.MdcFormTemplate;
import com.ananops.provider.model.dto.FormTemplateDto;
import com.ananops.provider.model.dto.FormTemplateItemDto;
import com.ananops.provider.service.MdcFormTemplateFeignApi;
import com.ananops.provider.service.MdcFormTemplateService;
import com.ananops.wrapper.WrapMapper;
import com.ananops.wrapper.Wrapper;
import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020/4/21 下午4:21
 */
@RestController
@Api(value = "API - MdcFormTemplateFeignClient", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class MdcFormTemplateFeignClient implements MdcFormTemplateFeignApi {

    @Resource
    private MdcFormTemplateService mdcFormTemplateService;

    @Resource
    private MdcFormTemplateMapper mdcFormTemplateMapper;

    @Resource
    private MdcFormTemplateItemMapper mdcFormTemplateItemMapper;

    @Override
    public Wrapper<FormTemplateDto> getFormTemplateByProjectId(@RequestParam("projectId") Long projectId) {
        if (projectId == null) {
            throw new BusinessException(ErrorCodeEnum.MDC10021039,projectId);
        }
        Example example = new Example(MdcFormTemplate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", projectId);
        List<MdcFormTemplate> mdcFormTemplates = mdcFormTemplateMapper.selectByExample(example);
        if (mdcFormTemplates == null) {
            throw new BusinessException(ErrorCodeEnum.MDC10021040,projectId);
        }
        MdcFormTemplate mdcFormTemplate = mdcFormTemplates.get(0);
        FormTemplateDto formTemplateDto = new FormTemplateDto();
        formTemplateDto.setId(mdcFormTemplate.getId());
        formTemplateDto.setSchemaId(mdcFormTemplate.getSchemaId());
        // 装入模板资产清单项
        List<FormTemplateItemDto> assetList = mdcFormTemplateService.queryItemsByTemplateId(mdcFormTemplate.getId(), "device");
        List<FormTemplateItemDto> inspcDetailList = mdcFormTemplateService.queryItemsByTemplateId(mdcFormTemplate.getId(), "inspcContent");
        formTemplateDto.setAssetList(assetList);
        formTemplateDto.setInspcDetailList(inspcDetailList);
        return WrapMapper.ok(formTemplateDto);
    }
}
