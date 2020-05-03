/*
 * Copyright (c) 2019. ananops.com All Rights Reserved.
 * 项目名称：ananops平台
 * 类名称：UacGroupBindUserController.java
 * 创建人：ananops
 * 平台官网: http://ananops.com
 */

package com.ananops.provider.web.admin;


import com.ananops.base.dto.LoginAuthDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.core.annotation.LogAnnotation;
import com.ananops.core.support.BaseController;
import com.ananops.provider.model.domain.UacUser;
import com.ananops.provider.model.dto.group.GroupBindUserDto;
import com.ananops.provider.model.dto.group.GroupBindUserReqDto;
import com.ananops.provider.model.dto.user.UserQueryDto;
import com.ananops.provider.model.exceptions.UacBizException;
import com.ananops.provider.service.UacGroupService;
import com.ananops.provider.service.UacUserService;
import com.ananops.wrapper.WrapMapper;
import com.ananops.wrapper.Wrapper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 组织绑定用户.
 *
 * @author ananops.com @gmail.com
 */
@RestController
@RequestMapping(value = "/group", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "Web - UacGroupBindUserController", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UacGroupBindUserController extends BaseController {

	@Resource
	private UacGroupService uacGroupService;

	@Resource
	private UacUserService uacUserService;

	/**
	 * 组织绑定用户
	 *
	 * @param groupBindUserReqDto the group bind user req dto
	 *
	 * @return the wrapper
	 */
	@PostMapping(value = "/bindUser")
	@LogAnnotation
	@ApiOperation(httpMethod = "POST", value = "组织绑定用户")
	public Wrapper bindUser4Role(@ApiParam(name = "uacGroupBindUserReqDto", value = "组织绑定用户") @RequestBody GroupBindUserReqDto groupBindUserReqDto) {
		logger.info("组织绑定用户...  groupBindUserReqDto={}", groupBindUserReqDto);
		LoginAuthDto loginAuthDto = super.getLoginAuthDto();
		uacGroupService.bindUacUser4Group(groupBindUserReqDto, loginAuthDto);
		return WrapMapper.ok();
	}

	/**
	 * 组织绑定用户页面数据
	 *
	 * @param groupId the group id
	 *
	 * @return the group bind user page info
	 */
	@PostMapping(value = "/getBindUser/{groupId}")
	@ApiOperation(httpMethod = "POST", value = "获取组织绑定用户页面数据")
	public Wrapper<GroupBindUserDto> getGroupBindUserPageInfo(@ApiParam(name = "groupId", value = "组织id") @PathVariable Long groupId) {
		logger.info("查询组织绑定用户页面数据 groupId={}", groupId);
		LoginAuthDto loginAuthDto = super.getLoginAuthDto();
		Long currentUserId = loginAuthDto.getUserId();
		GroupBindUserDto bindUserDto = uacGroupService.getGroupBindUserDto(groupId, currentUserId);
		return WrapMapper.ok(bindUserDto);
	}

	/**
	 * 查询组织下的用户列表
	 *
	 * @param userQueryDto 查询参数
	 *
	 * @return 返回成员列表
	 */
	@PostMapping(value = "/getUsersByGroupId")
	@ApiOperation(httpMethod = "POST", value = "查询该组织下的用户列表")
	public Wrapper<PageInfo> getUsersByGroupId(@ApiParam(name = "userQueryDto", value = "查询参数") @RequestBody UserQueryDto userQueryDto) {

		logger.info("查询该组织下的用户列表 userQueryDto={}", userQueryDto);
		if(userQueryDto.getGroupId() == null){
			throw new UacBizException(ErrorCodeEnum.UAC10015010, userQueryDto.getGroupId());
		}
		PageInfo pageInfo = uacUserService.queryUserListByGroupId(userQueryDto);
		return WrapMapper.ok(pageInfo);
	}
}
