package com.ananops.provider.web.rpc;

import com.ananops.core.support.BaseController;
import com.ananops.provider.model.dto.SysDictItemsDto;
import com.ananops.provider.service.MdcDictItemFeignApi;
import com.ananops.provider.service.MdcDictItemService;
import com.ananops.wrapper.WrapMapper;
import com.ananops.wrapper.Wrapper;
import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020-04-07 16:45
 */
@RestController
@Api(value = "API - MdcDictItemFeignClient", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class MdcDictItemFeignClient extends BaseController implements MdcDictItemFeignApi {

    @Resource
    private MdcDictItemService mdcDictItemService;

    @Override
    public Wrapper<SysDictItemsDto> getSysDictItems(@RequestParam("userId") Long userId) {
        SysDictItemsDto sysDictItemsDto = mdcDictItemService.getSysDictItems(userId);
        return WrapMapper.ok(sysDictItemsDto);
    }

}
