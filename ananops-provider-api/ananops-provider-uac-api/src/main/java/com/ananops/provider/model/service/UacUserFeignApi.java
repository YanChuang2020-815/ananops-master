package com.ananops.provider.model.service;

import com.ananops.base.dto.CheckValidDto;
import com.ananops.provider.model.dto.user.IdStatusDto;
import com.ananops.provider.model.dto.user.UserIdsReqDto;
import com.ananops.provider.model.dto.user.UserInfoDto;
import com.ananops.provider.model.dto.user.UserRegisterDto;
import com.ananops.provider.model.service.hystrix.UacUserFeignHystrix;
import com.ananops.security.feign.OAuth2FeignAutoConfiguration;
import com.ananops.wrapper.Wrapper;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 开放内部模块对UAC User的API
 *
 * Created by bingyueduan on 2019/12/29.
 */
@FeignClient(value = "ananops-provider-uac", configuration = OAuth2FeignAutoConfiguration.class, fallback = UacUserFeignHystrix.class)
public interface UacUserFeignApi {

    /**
     * 在UAC中注册用户.
     *
     * @return the wrapper
     */
    @PostMapping(value = "/api/uac/user/register")
    Wrapper<Long> userRegister(@RequestBody UserRegisterDto userRegisterDto);

    /**
     * 根据UserId获取用户信息
     *
     * @param userId
     *
     * @return
     */
    @PostMapping(value = "/api/uac/user/getUacUserById")
    Wrapper<UserInfoDto> getUacUserById(@RequestParam("userId") Long userId);

    /**
     * 在UAC中注册添加用户,设置默认密码及状态.
     *
     * @return the wrapper
     */
    @PostMapping(value = "/api/uac/user/add")
    Wrapper<Long> addUser(@RequestBody UserInfoDto userInfoDto);

    /**
     * 更改UAC中User的状态.
     *
     * @return the wrapper
     */
    @PostMapping(value = "/api/uac/user/modifyUserStatusById")
    Wrapper<Integer> modifyUserStatus(@RequestBody IdStatusDto modifyUserStatusDto);

    /**
     * 在UAC中更新用户.
     *
     * @return the wrapper
     */
    @PostMapping(value = "/api/uac/user/save")
    Wrapper<Long> userSave(@RequestBody UserInfoDto userInfoDto);

    /**
     * 验证用户是否存在以及身份是否属实
     *
     * @return the wrapper
     */
    @PostMapping(value = "/api/uac/user/validateUser/{userId}/{roleCode}")
    Wrapper<Boolean> validateUser(@RequestParam("userId") Long userId, @RequestParam("roleCode") String roleCode);

    /**
     * 通过传入的批量UserId集合查找用户信息集合
     *
     * @param userIdsReqDto 用户Id集合
     *
     * @return 用户信息集合
     */
    @PostMapping(value = "/api/uac/user/getUserListByUserIds")
    Wrapper<List<UserInfoDto>> getUserListByUserIds(@RequestBody UserIdsReqDto userIdsReqDto);

    /**
     * 校验数据
     *
     * @param checkValidDto 需要校验数据DTO
     *
     * @return
     */
    @PostMapping(value = "/api/uac/user/checkUserValid")
    Wrapper checkValid(@RequestBody CheckValidDto checkValidDto);

    /**
     * 根据用户Id删除用户记录
     *
     * @param userId
     *
     * @return
     */
    @PostMapping(value = "/api/uac/user/deleteByUserId")
    Wrapper<Integer> deleteUserById(@RequestParam("userId") Long userId);
}
