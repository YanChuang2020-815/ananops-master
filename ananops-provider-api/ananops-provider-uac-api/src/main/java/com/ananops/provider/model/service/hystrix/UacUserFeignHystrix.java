package com.ananops.provider.model.service.hystrix;

import com.ananops.base.dto.CheckValidDto;
import com.ananops.provider.model.dto.user.IdStatusDto;
import com.ananops.provider.model.dto.user.UserIdsReqDto;
import com.ananops.provider.model.dto.user.UserInfoDto;
import com.ananops.provider.model.dto.user.UserRegisterDto;
import com.ananops.provider.model.service.UacUserFeignApi;
import com.ananops.wrapper.Wrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * UAC模块User Feign Hystrix
 *
 * Created by bingyueduan on 2019/12/29.
 */
@Component
public class UacUserFeignHystrix implements UacUserFeignApi {

    @Override
    public Wrapper<Long> userRegister(@RequestBody UserRegisterDto userRegisterDto) {
        return null;
    }

    @Override
    public Wrapper<UserInfoDto> getUacUserById(@RequestParam("userId") Long userId) {
        return null;
    }

    @Override
    public Wrapper<Long> addUser(@RequestBody UserInfoDto userInfoDto) {
        return null;
    }

    @Override
    public Wrapper<Integer> modifyUserStatus(@RequestBody IdStatusDto modifyUserStatusDto) {
        return null;
    }

    @Override
    public Wrapper<Long> userSave(@RequestBody UserInfoDto userInfoDto) {
        return null;
    }

    @Override
    public Wrapper<Boolean> validateUser(Long userId, String roleCode) {
        return null;
    }

    @Override
    public Wrapper<List<UserInfoDto>> getUserListByUserIds(@RequestBody UserIdsReqDto userIdsReqDto) {
        return null;
    }

    @Override
    public Wrapper checkValid(CheckValidDto checkValidDto) {
        return null;
    }

    @Override
    public Wrapper<Integer> deleteUserById(Long userId) {
        return null;
    }
}
