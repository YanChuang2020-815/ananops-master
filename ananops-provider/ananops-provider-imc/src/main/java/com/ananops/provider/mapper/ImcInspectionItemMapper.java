package com.ananops.provider.mapper;

import com.ananops.core.mybatis.MyMapper;
import com.ananops.provider.model.domain.ImcInspectionItem;
import com.ananops.provider.model.domain.ImcInspectionTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ImcInspectionItemMapper extends MyMapper<ImcInspectionItem> {
    List<ImcInspectionItem> queryItemByUserId(@Param(value = "userId") Long userId);

    List<ImcInspectionItem> queryItemByUserIdAndStatus(@Param(value = "userId")Long userId, @Param(value = "status")Integer status);

    List<ImcInspectionItem> queryFinishedItemByMaintainerId(@Param(value = "maintainerId") Long maintainerId);
}