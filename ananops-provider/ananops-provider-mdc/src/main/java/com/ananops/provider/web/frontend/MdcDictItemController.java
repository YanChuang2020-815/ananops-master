package com.ananops.provider.web.frontend;

import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.BaseController;
import com.ananops.provider.model.domain.MdcSysDictItem;
import com.ananops.provider.model.dto.MdcAddDictItemDto;
import com.ananops.provider.model.dto.SysDictItemsDto;
import com.ananops.provider.service.MdcDictItemService;
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
 * Created by huqiaoqian on 2020/3/27
 */
@RestController
@RequestMapping(value = "/dictItem", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "WEB - MdcDictItemController", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class MdcDictItemController extends BaseController {
    @Resource
    MdcDictItemService dictItemService;

    /**
     * 创建或编辑字典项
     *
     * @param addDictItemDto HTTP请求参数
     *
     * @return 返回
     */
    @PostMapping(value = "/save")
    @ApiOperation(httpMethod = "POST",value = "创建或编辑字典项")
    public Wrapper<MdcAddDictItemDto> saveInspectionItem(@ApiParam(name = "saveDictItem",value = "创建或编辑字典项")@RequestBody MdcAddDictItemDto addDictItemDto){
        LoginAuthDto loginAuthDto = getLoginAuthDto();
        return WrapMapper.ok(dictItemService.saveItem(addDictItemDto,loginAuthDto));
    }


    /**
     * 根据字典库id获取字典项列表
     *
     * @param dictId HTTP请求参数
     *
     * @return 返回
     */
    @GetMapping(value = "/getDictItemListByDictId")
    @ApiOperation(httpMethod = "GET",value = "根据字典库id获取字典项列表")
    public Wrapper<List<MdcSysDictItem>> getDictItemListByDictId(@RequestParam("dictId") Long dictId){
        LoginAuthDto loginAuthDto = super.getLoginAuthDto();
        List<MdcSysDictItem> dictItemList=dictItemService.getDictItemListByDictId(dictId, loginAuthDto.getUserId());
        return WrapMapper.ok(dictItemList);
    }

    /**
     * 为维修工单页面提供准备数据
     *
     * @return 返回
     */
    @GetMapping(value = "/getSysDictItemList")
    @ApiOperation(httpMethod = "GET",value = "为维修工单页面提供准备数据")
    public Wrapper<SysDictItemsDto> getSysDictItemList(){
        LoginAuthDto loginAuthDto = super.getLoginAuthDto();
        SysDictItemsDto sysDictItemsDto = dictItemService.getSysDictItems(loginAuthDto.getUserId());
        return WrapMapper.ok(sysDictItemsDto);
    }

    /**
     * 根据字典项id删除字典项
     *
     * @param itemId HTTP请求参数
     *
     * @return 返回
     */
    @PostMapping(value = "/deleteDictItemByItemDictId/{itemId}")
    @ApiOperation(httpMethod = "POST",value = "根据字典库id删除字典库及其所属字典项")
    public Wrapper<MdcSysDictItem> deleteDictByDictId(@PathVariable Long itemId) {
        LoginAuthDto loginAuthDto = getLoginAuthDto();
        return WrapMapper.ok(dictItemService.deleteDictItemByItemId(itemId,loginAuthDto));
    }

}
