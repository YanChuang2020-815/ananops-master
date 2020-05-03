package com.ananops.provider.service.impl;

import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.core.support.BaseService;
import com.ananops.provider.mapper.UacRoleGroupMapper;
import com.ananops.provider.model.constant.RoleConstant;
import com.ananops.provider.model.domain.UacRoleGroup;
import com.ananops.provider.model.exceptions.UacBizException;
import com.ananops.provider.service.UacRoleGroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020-02-24 19:46
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UacRoleGroupServiceImpl extends BaseService<UacRoleGroup> implements UacRoleGroupService {

    @Resource
    private UacRoleGroupMapper uacRoleGroupMapper;

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<Long> listByGroupId(Long groupId) {
        if (groupId == null) {
            throw new UacBizException(ErrorCodeEnum.UAC10015010);
        }
        UacRoleGroup roleGroup = new UacRoleGroup();
        roleGroup.setGroupId(groupId);
        List<UacRoleGroup> list = uacRoleGroupMapper.select(roleGroup);
        List<Long> roleIds = new ArrayList<>();
        if (list != null) {
            for (UacRoleGroup uacRoleGroup : list) {
                roleIds.add(uacRoleGroup.getRoleId());
            }
        }
        return roleIds;
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        if (roleId == null) {
            throw new UacBizException(ErrorCodeEnum.UAC10012001);
        }
        UacRoleGroup roleGroup = new UacRoleGroup();
        roleGroup.setRoleId(roleId);
        uacRoleGroupMapper.delete(roleGroup);
    }

    @Override
    public void deleteByRoleIdList(List<Long> roleIdList) {
        uacRoleGroupMapper.deleteByRoleIdList(roleIdList);
    }

    @Override
    public int saveRolesGroup(Long groupId, List<Long> userDefaultRoleIds) {
        if (groupId == null) {
            throw new UacBizException(ErrorCodeEnum.UAC10015010);
        }
        for (Long roleId : userDefaultRoleIds) {
            UacRoleGroup uacRoleGroup = new UacRoleGroup();
            uacRoleGroup.setGroupId(groupId);
            uacRoleGroup.setRoleId(roleId);
            if (uacRoleGroupMapper.selectCount(uacRoleGroup) < 1) {
                uacRoleGroupMapper.insertSelective(uacRoleGroup);
            }
        }
        return 0;
    }

    @Override
    public void deleteDefaultByGroupId(Long groupId) {
        if (groupId == null) {
            throw new UacBizException(ErrorCodeEnum.UAC10015010);
        }
        List<Long> roleIdList = new ArrayList<>();
        roleIdList.addAll(RoleConstant.USER_DEFAULT_ROLE_IDS);
        roleIdList.addAll(RoleConstant.FAC_DEFAULT_ROLE_IDS);
        uacRoleGroupMapper.deleteByGroupIdAndRoleIds(groupId, roleIdList);
    }
}
