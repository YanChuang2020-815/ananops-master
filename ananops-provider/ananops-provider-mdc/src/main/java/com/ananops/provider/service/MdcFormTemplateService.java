package com.ananops.provider.service;

import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.IService;
import com.ananops.provider.model.domain.MdcFormTemplate;
import com.ananops.provider.model.dto.FormTemplateItemDto;
import com.ananops.provider.model.dto.FormDataDto;

import java.util.List;

/**
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020/4/20 下午3:30
 */

public interface MdcFormTemplateService extends IService<MdcFormTemplate> {

    /**
     * 保存表单模板
     *
     * @param mdcFormDataDto
     *
     * @param loginAuthDto
     *
     * @return
     */
    Integer saveFormTemplate(FormDataDto mdcFormDataDto, LoginAuthDto loginAuthDto);

    /**
     * 更新单个表单模板
     *
     * @param mdcFormTemplate
     *
     * @param loginAuthDto
     *
     * @return
     */
    Integer updateFormTemplate(MdcFormTemplate mdcFormTemplate, LoginAuthDto loginAuthDto);

    /**
     * 获取表单模板列表
     *
     * @param loginAuthDto
     *
     * @return
     */
    List<MdcFormTemplate> getFormTemplateList(LoginAuthDto loginAuthDto);

    /**
     * 根据模板Id查询模板详情
     *
     * @param templateId
     * @return
     */
    MdcFormTemplate queryById(Long templateId);

    /**
     * 根据模板Id查询模板详情(包括所有模板内容项)
     *
     * @param templateId
     *
     * @return
     */
    FormDataDto queryDetailsById(Long templateId);

    /**
     * 根据模板Id删除动态表单模板
     *
     * @param templateId
     *
     * @return
     */
    Integer deleteById(Long templateId);

    /**
     * 根据TemplateId查询模板子项
     *
     * @param id
     *
     * @param device
     *
     * @return
     */
    List<FormTemplateItemDto> queryItemsByTemplateId(Long templateId, String type);
}
