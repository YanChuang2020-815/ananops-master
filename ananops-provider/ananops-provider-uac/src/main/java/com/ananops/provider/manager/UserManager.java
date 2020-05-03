/*
 * Copyright (c) 2019. ananops.com All Rights Reserved.
 * 项目名称：ananops平台
 * 类名称：UserManager.java
 * 创建人：ananops
 * 平台官网: http://ananops.com
 */

package com.ananops.provider.manager;

import com.ananops.provider.model.constant.RoleConstant;
import com.ananops.provider.service.UacRoleGroupService;
import com.google.common.base.Preconditions;
import com.ananops.base.constant.GlobalConstant;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.provider.annotation.MqProducerStore;
import com.ananops.provider.mapper.UacRoleUserMapper;
import com.ananops.provider.mapper.UacUserMapper;
import com.ananops.provider.model.domain.MqMessageData;
import com.ananops.provider.model.domain.UacRoleUser;
import com.ananops.provider.model.domain.UacUser;
import com.ananops.provider.model.enums.MqSendTypeEnum;
import com.ananops.provider.model.exceptions.UacBizException;
import com.ananops.provider.service.impl.RedisServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * The class User manager.
 *
 * @author ananops.com @gmail.com
 */
@Slf4j
@Component
@Transactional(rollbackFor = Exception.class)
public class UserManager {

	@Resource
	private UacUserMapper uacUserMapper;

	@Resource
	private UacRoleUserMapper uacRoleUserMapper;

	@Resource
	private UacRoleGroupService uacRoleGroupService;

	@Resource
	private RedisServiceImpl redisService;

	@MqProducerStore(sendType = MqSendTypeEnum.SAVE_AND_SEND)
	public void submitResetPwdEmail(final MqMessageData messageData) {
		log.info("重置密码发送邮件. messageData={}", messageData);
	}

	@MqProducerStore
	public void register(final MqMessageData mqMessageData, final UacUser uacUser) {
		log.info("注册用户. mqMessageData={}, user={}", mqMessageData, uacUser);
		uacUserMapper.insertSelective(uacUser);
		UacRoleUser uacRoleUser = new UacRoleUser();
		uacRoleUser.setUserId(uacUser.getId());
		uacRoleUser.setRoleId(GlobalConstant.Spc.SPC_MANAGER_ROLE_ID);
		uacRoleUserMapper.insertSelective(uacRoleUser);
	}

	@MqProducerStore
	public void resetLoginPwd(final MqMessageData mqMessageData, final UacUser update) {
		log.info("重置密码. mqMessageData={}, user={}", mqMessageData, update);
		int updateResult = uacUserMapper.updateByPrimaryKeySelective(update);
		if (updateResult < 1) {
			log.error("用户【 {} 】重置密码失败", update.getLoginName());
		} else {
			log.info("用户【 {} 】重置密码成功", update.getLoginName());
		}
	}

	@MqProducerStore
	public void activeUser(final MqMessageData mqMessageData, final UacUser uacUser, final String activeUserKey) {
		log.info("激活用户. mqMessageData={}, user={}", mqMessageData, uacUser);
		int result = uacUserMapper.updateByPrimaryKeySelective(uacUser);
		if (result < 1) {
			throw new UacBizException(ErrorCodeEnum.UAC10011038, uacUser.getId());
		}

		// 绑定一个访客角色默认值roleId=10000
		final Long userId = uacUser.getId();
		final Long groupId = uacUser.getGroupId();
		Preconditions.checkArgument(userId != null, "用戶Id不能爲空");
		Preconditions.checkArgument(groupId != null, "组织Id不能爲空");

		// 为服务商管理员配置最初的服务商业务角色
		uacRoleGroupService.saveRolesGroup(groupId, RoleConstant.FAC_DEFAULT_ROLE_IDS);

		// 删除 activeUserToken
		redisService.deleteKey(activeUserKey);
	}

	@MqProducerStore(sendType = MqSendTypeEnum.SAVE_AND_SEND)
	public void sendSmsCode(final MqMessageData mqMessageData) {
		log.info("发送短信验证码. mqMessageData={}", mqMessageData);
	}

	@MqProducerStore(sendType = MqSendTypeEnum.SAVE_AND_SEND)
	public void sendEmailCode(final MqMessageData mqMessageData) {
		log.info("发送邮件验证码. mqMessageData={}", mqMessageData);
	}
}
