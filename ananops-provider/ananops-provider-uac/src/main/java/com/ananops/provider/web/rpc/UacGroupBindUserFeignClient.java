package com.ananops.provider.web.rpc;

import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.BaseController;
import com.ananops.provider.model.dto.group.GroupBindUserApiDto;
import com.ananops.provider.model.dto.group.GroupBindUserReqDto;
import com.ananops.provider.model.service.UacGroupBindUserFeignApi;
import com.ananops.provider.service.UacGroupService;
import com.ananops.provider.service.UacGroupUserService;
import com.ananops.wrapper.WrapMapper;
import com.ananops.wrapper.Wrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 操作GroupBindUser
 *
 * Created by bingyueduan on 2020/1/15.
 */
@RestController
@Api(value = "API - UacGroupBindUserFeignClient", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UacGroupBindUserFeignClient extends BaseController implements UacGroupBindUserFeignApi {

    @Resource
    private UacGroupService uacGroupService;

    @Resource
    private UacGroupUserService uacGroupUserService;

    @Override
    @ApiOperation(httpMethod = "POST", value = "绑定用户到组织")
    public Wrapper bindUacUser4Group(@RequestBody GroupBindUserApiDto groupBindUserApiDto) {
        logger.info("组织绑定用户...  groupBindUserApiDto={}", groupBindUserApiDto);
        LoginAuthDto loginAuthDto = super.getLoginAuthDto();
        GroupBindUserReqDto groupBindUserReqDto = new GroupBindUserReqDto();
        try {
            BeanUtils.copyProperties(groupBindUserReqDto, groupBindUserApiDto);
        } catch (Exception e) {
            logger.error("用户组Dto与用户组状态传输Dto属性拷贝异常");
            e.printStackTrace();
        }
        uacGroupService.bindUacUser4Group(groupBindUserReqDto, loginAuthDto);
        return WrapMapper.ok();
    }

    @Override
    @ApiOperation(httpMethod = "POST", value = "根据UserId查询GroupId")
    public Wrapper<Long> getGroupIdByUserId(@RequestParam("userId") Long userId) {
        logger.info("根据UserId查询GroupId...  userId={}", userId);
        Long groupId = uacGroupUserService.queryByUserId(userId).getGroupId();
        return WrapMapper.ok(groupId);
    }

    @Override
    @ApiOperation(httpMethod = "POST", value = "根据UserId查询其公司组织的GroupId")
    public Wrapper<Long> getCompanyGroupIdByUserId(@RequestParam("userId") Long userId) {
        logger.info("根据UserId查询其公司组织的GroupId...  userId={}", userId);
        Long groupId = uacGroupUserService.queryCompanyGroupIdByUserId(userId);
        return WrapMapper.ok(groupId);
    }
}
