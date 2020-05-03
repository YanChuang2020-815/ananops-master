package com.ananops.provider.web.rpc;

import com.ananops.base.dto.CheckValidDto;
import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.BaseController;
import com.ananops.provider.model.constant.UacApiConstant;
import com.ananops.provider.model.domain.UacRole;
import com.ananops.provider.model.domain.UacUser;
import com.ananops.provider.model.dto.user.IdStatusDto;
import com.ananops.provider.model.dto.user.UserIdsReqDto;
import com.ananops.provider.model.dto.user.UserInfoDto;
import com.ananops.provider.model.dto.user.UserRegisterDto;
import com.ananops.provider.model.service.UacUserFeignApi;
import com.ananops.provider.service.UacRoleService;
import com.ananops.provider.service.UacUserService;
import com.ananops.wrapper.WrapMapper;
import com.ananops.wrapper.Wrapper;
import com.google.common.base.Preconditions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 操作User
 *
 * Created by bingyueduan on 2019/12/29.
 */
@RestController
@Api(value = "API - UacUserFeignClient", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UacUserFeignClient extends BaseController implements UacUserFeignApi {

    @Resource
    private UacUserService uacUserService;

    @Resource
    private UacRoleService uacRoleService;

    @Override
    @ApiOperation(httpMethod = "POST", value = "注册用户")
    public Wrapper<Long> userRegister(@RequestBody UserRegisterDto userRegisterDto) {
        logger.info("userRegister - 注册用户. userRegisterDto={}", userRegisterDto);
        return WrapMapper.ok(uacUserService.register(userRegisterDto));
    }

    @Override
    @ApiOperation(httpMethod = "POST", value = "根据用户Id查询用户信息")
    public Wrapper<UserInfoDto> getUacUserById(@RequestParam("userId") Long userId) {
        UserInfoDto userInfoDto = new UserInfoDto();
        logger.info("getUacUserById - 根据用户Id查询用户信息. userId={}", userId);
        UacUser uacUser = uacUserService.queryByUserId(userId);
        List<UacRole> roleList = uacRoleService.findAllRoleInfoByUserId(userId);
        Iterator<UacRole> iterator = roleList.iterator();
        if (iterator.hasNext()) {
            UacRole uacRole = iterator.next();
            userInfoDto.setRoleId(uacRole.getId());
            userInfoDto.setRoleName(uacRole.getRoleName());
            userInfoDto.setRoleCode(uacRole.getRoleCode());
            userInfoDto.setRoleStatus(uacRole.getStatus());
        }
        logger.info("getUacUserById - 根据用户Id查询用户信息. [OK] uacUser={}", uacUser);
        if (uacUser != null) {
            uacUser.setRoleId(userInfoDto.getRoleId());
            uacUser.setRoleCode(userInfoDto.getRoleCode());
            uacUser.setRoleName(userInfoDto.getRoleName());
            try {
                BeanUtils.copyProperties(userInfoDto, uacUser);
            } catch (Exception e) {
                logger.error("服务商Dto与用户Dto属性拷贝异常");
                e.printStackTrace();
            }
        }
        return WrapMapper.ok(userInfoDto);
    }

    @Override
    @ApiOperation(httpMethod = "POST", value = "添加工程师用户")
    public Wrapper<Long> addUser(@RequestBody UserInfoDto userInfoDto) {
        logger.info("添加工程师用户. userInfoDto={}", userInfoDto);
        return WrapMapper.ok(uacUserService.addUser(userInfoDto));
    }

    @Override
    @ApiOperation(httpMethod = "POST", value = "根据用户Id修改用户状态")
    public Wrapper<Integer> modifyUserStatus(@RequestBody IdStatusDto idStatusDto) {
        LoginAuthDto loginAuthDto = getLoginAuthDto();
        UacUser uacUser = new UacUser();
        uacUser.setId(idStatusDto.getId());
        int result = uacUserService.modifyUserStatusById(uacUser, loginAuthDto);
        return WrapMapper.ok(result);
    }

    @Override
    @ApiOperation(httpMethod = "POST", value = "更新用户信息")
    public Wrapper<Long> userSave(@RequestBody UserInfoDto userInfoDto) {
        LoginAuthDto loginAuthDto = super.getLoginAuthDto();
        UacUser uacUser = new UacUser();
        try {
            BeanUtils.copyProperties(uacUser, userInfoDto);
        } catch (Exception e) {
            logger.error("用户Dto与用户传输Dto属性拷贝异常");
            e.printStackTrace();
        }
        uacUserService.saveUacUser(uacUser, loginAuthDto);
        return WrapMapper.ok(userInfoDto.getId());
    }

    @Override
    @ApiOperation(httpMethod = "POST", value = "验证用户是否存在以及身份是否属实")
    public Wrapper<Boolean> validateUser(Long userId, String roleCode) {
        UacUser user = uacUserService.queryByUserId(userId);
        if (user == null) {
            return WrapMapper.ok(Boolean.FALSE);
        }
        UacRole role = uacRoleService.selectByKey(userId);
        if (!role.getRoleCode().equals(roleCode)) {
            return WrapMapper.ok(Boolean.FALSE);
        }
        return WrapMapper.ok(Boolean.TRUE);

    }

    @Override
    @ApiOperation(httpMethod = "POST", value = "通过传入的批量UserId集合查找用户信息集合")
    public Wrapper<List<UserInfoDto>> getUserListByUserIds(@RequestBody UserIdsReqDto userIdsReqDto) {
        logger.info("批量用户Ids. userIdsReqDto={}", userIdsReqDto);
        List<UserInfoDto> userInfoDtos = new ArrayList<>();
        if (userIdsReqDto.getUserIds() != null) {
            List<UacUser> uacUsers = uacUserService.batchGetUserInfo(userIdsReqDto.getUserIds());
            for (UacUser uacUser : uacUsers) {
                UserInfoDto userInfoDto = new UserInfoDto();
                try {
                    BeanUtils.copyProperties(userInfoDto, uacUser);
                } catch (Exception e) {
                    logger.error("用户Dto与用户传输Dto属性拷贝异常");
                    e.printStackTrace();
                }
                userInfoDtos.add(userInfoDto);
            }
        }
        return WrapMapper.ok(userInfoDtos);
    }

    @Override
    @ApiOperation(httpMethod = "POST", value = "校验数据")
    public Wrapper checkValid(@RequestBody CheckValidDto checkValidDto) {
        String type = checkValidDto.getType();
        String validValue = checkValidDto.getValidValue();

        Preconditions.checkArgument(StringUtils.isNotEmpty(validValue), "参数错误");
        String message = null;
        boolean result = false;
        //开始校验
        if (UacApiConstant.Valid.LOGIN_NAME.equals(type)) {
            result = uacUserService.checkLoginName(validValue);
            if (!result) {
                message = "用户名已存在";
            }
        }
        if (UacApiConstant.Valid.EMAIL.equals(type)) {
            result = uacUserService.checkEmail(validValue);
            if (!result) {
                message = "邮箱已存在";
            }
        }

        if (UacApiConstant.Valid.MOBILE_NO.equals(type)) {
            result = uacUserService.checkMobileNo(validValue);
            if (!result) {
                message = "手机号码已存在";
            }
        }

        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, message, result);
    }

    @Override
    @ApiOperation(httpMethod = "POST", value = "根据用户Id删除用户记录")
    public Wrapper<Integer> deleteUserById(@RequestParam("userId") Long userId) {
        int result = uacUserService.deleteUserById(userId);
        return WrapMapper.ok(result);
    }
}
