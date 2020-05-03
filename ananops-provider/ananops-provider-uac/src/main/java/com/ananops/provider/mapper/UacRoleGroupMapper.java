package com.ananops.provider.mapper;

import com.ananops.core.mybatis.MyMapper;
import com.ananops.provider.model.domain.UacRoleGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Uac Group-Role mapper.
 *
 * @author Duan Bingyue
 */
@Mapper
@Component
public interface UacRoleGroupMapper extends MyMapper<UacRoleGroup> {

    /**
     * 批量通过角色Id删除与Group的关联表记录
     *
     * @param roleIdList 角色ID集合
     */
    int deleteByRoleIdList(@Param("roleIdList") List<Long> roleIdList);

    /**
     * 批量通过角色Id删除与Group的关联表记录
     *
     * @param groupId groupId
     *
     * @param roleIds roleIds
     */
    int deleteByGroupIdAndRoleIds(@Param("groupId") Long groupId, @Param("roleIdList") List<Long> roleIdList);
}