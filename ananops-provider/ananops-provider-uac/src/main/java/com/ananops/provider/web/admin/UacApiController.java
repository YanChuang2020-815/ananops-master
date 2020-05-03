package com.ananops.provider.web.admin;

import com.ananops.base.dto.BaseQuery;
import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.BaseController;
import com.ananops.core.utils.RequestUtil;
import com.ananops.provider.model.domain.UacApi;
import com.ananops.provider.service.UacApiService;
import com.ananops.wrapper.WrapMapper;
import com.ananops.wrapper.Wrapper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created By ChengHao On 2020/4/7
 */
@RestController
@RequestMapping(value = "/acl", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "Web - UacApiMainController", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UacApiController extends BaseController {
    @Autowired
    UacApiService uacApiService;

    /**
     * 编辑api信息
     *
     * @param uacApi
     * @return
     */
    @PostMapping(value = "/save")
    @ApiOperation(httpMethod = "POST", value = "编辑api信息,当id为空时新增api,不为空时为更新api信息")
    public Wrapper save(@ApiParam(name = "api", value = "api信息") @RequestBody UacApi uacApi) {
        LoginAuthDto loginAuthDto = RequestUtil.getLoginUser();
        uacApiService.saveApi(uacApi, loginAuthDto);
        return WrapMapper.ok();
    }

    /**
     * 分页查询 api 列表
     *
     * @param baseQuery
     * @return
     */
    @PostMapping("/getApiList")
    @ApiOperation(httpMethod = "POST", value = "分页获取Api列表")
    public Wrapper<PageInfo> getApiList(@RequestBody BaseQuery baseQuery) {
        PageInfo pageInfo = uacApiService.getApiList(baseQuery);
        return WrapMapper.ok(pageInfo);
    }

    /**
     * 查询 apis
     *
     * @return
     */
    @PostMapping("/getApis")
    @ApiOperation(httpMethod = "POST", value = "获取Api列表")
    public Wrapper<List<UacApi>> getApis() {
        List<UacApi> uacApis = uacApiService.getApi();
        return WrapMapper.ok(uacApis);
    }


    /**
     * 查看 api
     *
     * @param id
     * @return
     */
    @PostMapping("/getApiById/{id}")
    @ApiOperation(httpMethod = "POST", value = "根据id,查看api")
    public Wrapper<UacApi> getApiById(@PathVariable Long id) {
        UacApi uacApi = uacApiService.getApiById(id);
        return WrapMapper.ok(uacApi);
    }

    /**
     * 删除 api
     *
     * @param id
     * @return
     */
    @PostMapping("/deleteApiById/{id}")
    @ApiOperation(httpMethod = "POST", value = "删除api")
    public Wrapper deleteApiById(@PathVariable Long id) {
        int result = uacApiService.deleteApiById(id);
        return WrapMapper.ok(result);
    }

}
