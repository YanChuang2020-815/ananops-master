/*
 * Copyright (c) 2019. ananops.com All Rights Reserved.
 * 项目名称：ananops平台
 * 类名称：UacGroupMapper.java
 * 创建人：ananops
 * 平台官网: http://ananops.com
 */

package com.ananops.provider.mapper;

import com.ananops.core.mybatis.MyMapper;
import com.ananops.provider.model.domain.UacGroup;
import com.ananops.provider.model.dto.role.BindUserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The interface Uac group mapper.
 *
 * @author ananops.com@gmail.com
 */
@Mapper
@Component
public interface UacGroupMapper extends MyMapper<UacGroup> {

    /**
     * 根据用户Id查询组织列表
     *
     * @param userId 用户Id
     *
     * @return 组织列表
     */
    List<UacGroup> selectGroupListByUserId(Long userId);

    /**
     * 通过组织名称查询组织列表
     *
     * @param groupName 组织名称
     *
     * @return 组织列表
     */
    List<UacGroup> selectGroupByGroupName(String groupName);

    /**
     * 查询一个GroupId下所绑定的所有User
     *
     * @param superManagerRoleId 超级用户管理员角色Id
     *
     * @param groupId 组织Id
     *
     * @param currentUserId 当前登录用户的Id
     *
     * @return 返回板顶用户信息
     */
    List<BindUserDto> selectAllUserByGroupId(@Param("superManagerRoleId") Long superManagerRoleId, @Param("groupId") Long groupId, @Param("currentUserId") Long currentUserId);
}