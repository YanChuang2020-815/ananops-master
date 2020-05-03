package com.ananops.provider.service;

import com.ananops.base.dto.BaseQuery;
import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.IService;
import com.ananops.provider.model.domain.UacApi;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * Created By ChengHao On 2020/4/7
 */
public interface UacApiService extends IService<UacApi> {

    /**
     * 编辑 api 信息
     *
     * @param uacApi
     * @param loginAuthDto
     */
    void saveApi(UacApi uacApi, LoginAuthDto loginAuthDto);

    /**
     * 分页查询 api 列表
     *
     * @param baseQuery
     * @return
     */
    PageInfo getApiList(BaseQuery baseQuery);

    /**
     * 查询 apis
     *
     * @return
     */
    List<UacApi> getApi();

    /**
     * 查看 api
     *
     * @param id
     * @return
     */
    UacApi getApiById(Long id);

    /**
     * 删除 api
     *
     * @param id
     * @return
     */
    int deleteApiById(Long id);


}
