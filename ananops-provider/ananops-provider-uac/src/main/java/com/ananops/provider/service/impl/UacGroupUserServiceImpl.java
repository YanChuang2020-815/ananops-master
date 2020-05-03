package com.ananops.provider.service.impl;

import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.core.support.BaseService;
import com.ananops.provider.mapper.UacGroupMapper;
import com.ananops.provider.mapper.UacGroupUserMapper;
import com.ananops.provider.model.domain.UacGroup;
import com.ananops.provider.model.domain.UacGroupUser;
import com.ananops.provider.model.exceptions.UacBizException;
import com.ananops.provider.model.vo.GroupZtreeVo;
import com.ananops.provider.service.UacGroupService;
import com.ananops.provider.service.UacGroupUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * The class Uac group user service.
 *
 * @author ananops.com@gmail.com
 */
@Service
public class UacGroupUserServiceImpl extends BaseService<UacGroupUser> implements UacGroupUserService {

	@Resource
	private UacGroupUserMapper uacGroupUserMapper;

	@Resource
	private UacGroupMapper uacGroupMapper;

	@Resource
	private UacGroupService uacGroupService;

	@Override
	public UacGroupUser queryByUserId(Long userId) {
		return uacGroupUserMapper.getByUserId(userId);
	}

	@Override
	public int updateByUserId(UacGroupUser uacGroupUser) {
		return uacGroupUserMapper.updateByUserId(uacGroupUser);
	}

	@Override
	public List<UacGroup> getGroupListByUserId(Long userId) {
		return uacGroupMapper.selectGroupListByUserId(userId);
	}

	@Override
	public void saveUserGroup(Long userId, Long groupId) {
		UacGroupUser groupUser = new UacGroupUser();
		groupUser.setUserId(userId);
		groupUser.setGroupId(groupId);
		uacGroupUserMapper.insertSelective(groupUser);
	}

	@Override
	public Long queryCompanyGroupIdByUserId(Long userId) {
		Long groupId = uacGroupUserMapper.getByUserId(userId).getGroupId();
		if (groupId == null) {
			throw new UacBizException(ErrorCodeEnum.UAC10015001, groupId);
		}

		// 获取该用户所属组织的公司组织
		UacGroup uacGroup = uacGroupService.getCompanyInfo(groupId);
		return uacGroup.getId();
	}
}
