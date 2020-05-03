package com.ananops.provider.model.service;

import com.ananops.provider.model.dto.group.GroupBindUserApiDto;
import com.ananops.provider.model.service.hystrix.UacGroupBindUserFeignHystrix;
import com.ananops.security.feign.OAuth2FeignAutoConfiguration;
import com.ananops.wrapper.Wrapper;
import javafx.scene.control.Dialog;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 开放内部模块对UAC GroupBindUser的API
 *
 * Created by bingyueduan on 2020/1/15.
 */
@FeignClient(value = "ananops-provider-uac", configuration = OAuth2FeignAutoConfiguration.class, fallback = UacGroupBindUserFeignHystrix.class)
public interface UacGroupBindUserFeignApi {

    /**
     * 用户绑定组织DTO
     *
     * @param groupBindUserDto 请求参数
     *
     * @return
     */
    @PostMapping(value = "/api/uac/group/bindUser")
    Wrapper bindUacUser4Group(@RequestBody GroupBindUserApiDto groupBindUserDto);

    /**
     * 根据User Id查询Group Id
     *
     * @param userId 参数
     *
     * @return Group Id
     */
    @PostMapping(value = "/api/uac/group/getGroupIdByUserId")
    Wrapper<Long> getGroupIdByUserId(@RequestParam("userId") Long userId);

    /**
     * 根据User Id查询其公司对应的Group Id
     *
     * @param userId 参数
     *
     * @return 公司组织的GroupId
     */
    @PostMapping(value = "/api/uac/group/getCompanyGroupIdByUserId")
    Wrapper<Long> getCompanyGroupIdByUserId(@RequestParam("userId") Long userId);
}
