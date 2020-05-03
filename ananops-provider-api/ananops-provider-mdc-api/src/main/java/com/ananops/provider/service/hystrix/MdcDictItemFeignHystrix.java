package com.ananops.provider.service.hystrix;

import com.ananops.provider.model.dto.SysDictItemsDto;
import com.ananops.provider.service.MdcDictItemFeignApi;
import com.ananops.wrapper.Wrapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020-04-07 16:21
 */
@Component
public class MdcDictItemFeignHystrix implements MdcDictItemFeignApi {

    @Override
    public Wrapper<SysDictItemsDto> getSysDictItems(Long userId) {
        return null;
    }
}
