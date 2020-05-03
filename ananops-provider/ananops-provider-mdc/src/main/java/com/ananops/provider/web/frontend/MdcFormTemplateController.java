package com.ananops.provider.web.frontend;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.BaseController;
import com.ananops.provider.model.domain.MdcFormTemplate;
import com.ananops.provider.model.dto.FormDataDto;
import com.ananops.provider.service.MdcFormTemplateService;
import com.ananops.wrapper.WrapMapper;
import com.ananops.wrapper.Wrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 动态表单模板
 *
 * @author Bingyue Duan
 *
 * @version 1.0
 *
 * @date 2020/4/20 下午3:28
 */
@RestController
@RequestMapping(value = "/formTemplate", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "WEB - MdcFormTemplateController", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class MdcFormTemplateController extends BaseController {

    @Resource
    private MdcFormTemplateService mdcFormTemplateService;

    /**
     * 保存全部模板内容
     *
     * @return 返回
     */
    @PostMapping(value = "/saveFormTemplate")
    @ApiOperation(httpMethod = "POST",value = "保存全部模板内容")
    public Wrapper<Integer> saveFullFormTemplate(@ApiParam(name = "mdcFormDataDto",value = "添加或编辑动态表单模板")@RequestBody JSONObject jsonObject) {
        FormDataDto mdcFormDataDto = JSON.parseObject(jsonObject.toJSONString(), FormDataDto.class);
        logger.info("保存全部模板内容,mdcFormDataDto={}",jsonObject);
        LoginAuthDto loginAuthDto = super.getLoginAuthDto();
        return WrapMapper.ok(mdcFormTemplateService.saveFormTemplate(mdcFormDataDto, loginAuthDto));
    }

    /**
     * 修改模板单项
     *
     * @return 返回
     */
    @PostMapping(value = "/update")
    @ApiOperation(httpMethod = "POST",value = "修改模板单项")
    public Wrapper<Integer> updateFormTemplate(@ApiParam(name = "mdcFormTemplate",value = "添加或编辑动态表单模板单项")@RequestBody MdcFormTemplate mdcFormTemplate) {
        logger.info("获取巡检表单的表结构,mdcFormTemplate={}", mdcFormTemplate);
        LoginAuthDto loginAuthDto = super.getLoginAuthDto();
        return WrapMapper.ok(mdcFormTemplateService.updateFormTemplate(mdcFormTemplate, loginAuthDto));
    }

    /**
     * 获取表单模板列表
     *
     * @return 返回
     */
    @GetMapping(value = "/getFormTemplateList")
    @ApiOperation(httpMethod = "GET",value = "获取表单模板列表")
    public Wrapper<List<MdcFormTemplate>> getFormTemplateList() {
        logger.info("获取表单模板列表");
        LoginAuthDto loginAuthDto = super.getLoginAuthDto();
        return WrapMapper.ok(mdcFormTemplateService.getFormTemplateList(loginAuthDto));
    }

    /**
     * 根据表单模板Id查询表单模板详情->用作表单模板绑定
     *
     * @return 返回
     */
    @PostMapping(value = "/queryById/{templateId}")
    @ApiOperation(httpMethod = "GET",value = "查询表单模板详情")
    public Wrapper<MdcFormTemplate> queryById(@PathVariable Long templateId) {
        logger.info("查询表单模板详情,templateId={}",templateId);
        return WrapMapper.ok(mdcFormTemplateService.queryById(templateId));
    }

    /**
     * 根据表单模板Id查询表单模板包括所有内容详情->用作编辑项回显
     *
     * @return 返回
     */
    @PostMapping(value = "/queryDetailsById/{templateId}")
    @ApiOperation(httpMethod = "GET",value = "查询表单模板详情（包括内容项）")
    public Wrapper<FormDataDto> queryDetailsById(@PathVariable Long templateId) {
        logger.info("查询表单模板详情（包括内容项）,templateId={}",templateId);
        return WrapMapper.ok(mdcFormTemplateService.queryDetailsById(templateId));
    }

    /**
     * 根据表单模板Id删除动态表单模板
     *
     * @return 返回
     */
    @PostMapping(value = "/deleteFormTemplateById/{templateId}")
    @ApiOperation(httpMethod = "GET",value = "根据表单模板Id删除动态表单模板")
    public Wrapper<Integer> deleteById(@PathVariable Long templateId) {
        logger.info("根据表单模板Id删除动态表单模板,templateId={}",templateId);
        return WrapMapper.ok(mdcFormTemplateService.deleteById(templateId));
    }
}
