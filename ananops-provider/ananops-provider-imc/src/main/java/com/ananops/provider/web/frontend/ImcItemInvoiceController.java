package com.ananops.provider.web.frontend;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.BaseController;
import com.ananops.provider.core.annotation.AnanLogAnnotation;
import com.ananops.provider.model.domain.ImcItemInvoice;
import com.ananops.provider.model.dto.FormDataDto;
import com.ananops.provider.model.dto.ImcInvoiceQueryDto;
import com.ananops.provider.model.dto.oss.ElementImgUrlDto;
import com.ananops.provider.service.ImcItemInvoiceService;
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
 * 巡检单控制器
 *
 * @author Bingyue Duan
 *
 * @version 1.0
 *
 * @date 2020/4/21 下午8:19
 */
@RestController
@RequestMapping(value = "/itemInvoice",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "WEB - ImcItemInvoiceController",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ImcItemInvoiceController extends BaseController {

    @Resource
    private ImcItemInvoiceService imcItemInvoiceService;

    /**
     * 查询巡检单列表
     *
     * @param imcInvoiceQueryDto 查询参数
     *
     * @return
     */
    @PostMapping(value = "/queryInvoiceList")
    @ApiOperation(httpMethod = "POST",value = "查询巡检单据列表")
    @AnanLogAnnotation
    public Wrapper<List<ImcItemInvoice>> queryInvoiceList(@ApiParam(name = "imcInvoiceQueryDto",value = "巡检单查询参数")@RequestBody ImcInvoiceQueryDto imcInvoiceQueryDto){
        logger.info("查询巡检单据列表,imcInvoiceQueryDto={}", imcInvoiceQueryDto);
        LoginAuthDto loginAuthDto = getLoginAuthDto();
        return WrapMapper.ok(imcItemInvoiceService.queryInvoiceList(imcInvoiceQueryDto,loginAuthDto));
    }

    /**
     * 根据巡检单Id查询详情
     *
     * @param invoiceId 巡检单Id
     *
     * @return
     */
    @PostMapping(value = "/queryDetailsById/{invoiceId}")
    @ApiOperation(httpMethod = "POST",value = "根据巡检单据的ID，获取对应的巡检单据内容详情")
    public Wrapper<FormDataDto> queryDetailsById(@PathVariable Long invoiceId){
        logger.info("获取对应的巡检单据内容详情,invoiceId={}", invoiceId);
        return WrapMapper.ok(imcItemInvoiceService.queryDetailsById(invoiceId));
    }

    /**
     * 保存更新巡检单数据
     *
     * @return 返回
     */
    @PostMapping(value = "/save")
    @ApiOperation(httpMethod = "POST",value = "保存更新巡检单数据")
    public Wrapper<Integer> save(@ApiParam(name = "formDataDto",value = "添加或编辑动态表单模板")@RequestBody JSONObject jsonObject) {
        logger.info("保存更新巡检单数据,formDataDto={}",jsonObject.toJSONString());
        FormDataDto formDataDto = JSON.parseObject(jsonObject.toJSONString(), FormDataDto.class);
        LoginAuthDto loginAuthDto = super.getLoginAuthDto();
        return WrapMapper.ok(imcItemInvoiceService.saveData(formDataDto, loginAuthDto));
    }

    /**
     * 查看已完成的巡检单预览文件
     *
     * @param itemId 巡检任务子项ID
     *
     * @return
     */
    @GetMapping(value = "/getInvoicePreview/{itemId}")
    @ApiOperation(httpMethod = "GET",value = "查看已完成的巡检单预览文件")
    public Wrapper<List<ElementImgUrlDto>> getInvoicePreview(@PathVariable Long itemId){
        logger.info("查看已完成的巡检单预览文件,itemId={}",itemId);
        LoginAuthDto loginAuthDto = super.getLoginAuthDto();
        return WrapMapper.ok(imcItemInvoiceService.getInvoicePreview(itemId, loginAuthDto));
    }
}
