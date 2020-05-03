package com.ananops.provider.service;

import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.IService;
import com.ananops.provider.model.domain.MdcFormSchema;
import com.ananops.provider.model.dto.MdcFormSchemaDto;

import java.util.List;

/**
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020/4/20 上午10:11
 */
public interface MdcFormSchemaService extends IService<MdcFormSchema> {

    /**
     * 用户信息从loginAuthDto获取
     *
     * @param loginAuthDto
     *
     * @return
     */
    List<MdcFormSchemaDto> getInspcFormSchema(LoginAuthDto loginAuthDto);

}
