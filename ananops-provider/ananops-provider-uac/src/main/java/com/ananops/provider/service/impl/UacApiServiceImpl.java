package com.ananops.provider.service.impl;

import com.ananops.base.dto.BaseQuery;
import com.ananops.base.dto.LoginAuthDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.core.support.BaseService;
import com.ananops.provider.mapper.UacApiMapper;
import com.ananops.provider.model.domain.MqMessageData;
import com.ananops.provider.model.domain.UacApi;
import com.ananops.provider.model.exceptions.UacBizException;
import com.ananops.provider.service.UacApiService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created By ChengHao On 2020/4/7
 */
@Service
public class UacApiServiceImpl extends BaseService<UacApi> implements UacApiService {

    @Resource
    UacApiMapper uacApiMapper;

    @Override
    public void saveApi(UacApi uacApi, LoginAuthDto loginAuthDto) {
        uacApi.setUpdateInfo(loginAuthDto);
        if (uacApi.isNew()) {
            uacApi.setId(super.generateId());
            uacApiMapper.insertSelective(uacApi);
        } else {
            int result = uacApiMapper.updateByPrimaryKeySelective(uacApi);
            if (result < 1) {
                throw new UacBizException(ErrorCodeEnum.UAC10015013, uacApi.getId());
            }
        }
    }

    @Override
    public PageInfo getApiList(BaseQuery baseQuery) {
        PageHelper.startPage(baseQuery.getPageNum(), baseQuery.getPageSize());
        List<UacApi> uacApiList = uacApiMapper.selectAll();
        return new PageInfo<>(uacApiList);
    }

    @Override
    public List<UacApi> getApi() {
        return uacApiMapper.selectAll();
    }

    @Override
    public UacApi getApiById(Long id) {
        return uacApiMapper.selectByPrimaryKey(id);
    }

    @Override
    public int deleteApiById(Long id) {
        return uacApiMapper.deleteByPrimaryKey(id);
    }


}
