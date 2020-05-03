package com.ananops.provider.mapper;

import com.ananops.core.mybatis.MyMapper;
import com.ananops.provider.model.domain.ImcInspectionTask;
import com.ananops.provider.model.dto.UndistributedImcTaskDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ImcInspectionTaskMapper extends MyMapper<ImcInspectionTask> {
    Integer countTaskByUserId(@Param(value = "userId") Long userId);

    List<ImcInspectionTask> queryTaskByUserId(@Param(value = "userId") Long userId);

    List<ImcInspectionTask> queryTaskByUserIdAndTaskName(@Param(value = "userId") Long userId,@Param(value = "taskName")String taskName);

    List<ImcInspectionTask> queryTaskByUserIdAndStatus(@Param(value = "userId")Long userId,@Param(value = "status")Integer status);

    List<ImcInspectionTask> queryTaskByUserIdAndStatusAndTaskName(@Param(value = "userId")Long userId,@Param(value = "status")Integer status,@Param(value = "taskName")String taskName);

    List<ImcInspectionTask> queryTaskByFacilitatorManagerId(@Param(value = "facilitatorManagerId")Long facilitatorManagerId);

    List<ImcInspectionTask> queryTaskByFacilitatorManagerIdAndTaskName(@Param(value = "facilitatorManagerId")Long facilitatorManagerId,@Param(value = "taskName")String taskName);

    List<ImcInspectionTask> queryTaskByFacilitatorManagerIdAndStatus(@Param(value = "facilitatorManagerId")Long facilitatorManagerId,@Param(value = "status")Integer status);

    List<ImcInspectionTask> queryTaskByFacilitatorManagerIdAndStatusAndTaskName(@Param(value = "facilitatorManagerId")Long facilitatorManagerId,@Param(value = "status")Integer status,@Param(value = "taskName")String taskName);

    List<ImcInspectionTask> queryTaskByFacilitatorGroupId(@Param(value = "facilitatorGroupId")Long facilitatorGroupId);

    List<ImcInspectionTask> queryTaskByFacilitatorGroupIdAndTaskName(@Param(value = "facilitatorGroupId")Long facilitatorGroupId,@Param(value = "taskName")String taskName);

    List<ImcInspectionTask> queryTaskByFacilitatorGroupIdAndStatus(@Param(value = "facilitatorGroupId")Long facilitatorGroupId,@Param(value = "status")Integer status);

    List<ImcInspectionTask> queryTaskByFacilitatorGroupIdAndStatusAndTaskName(@Param(value = "facilitatorGroupId")Long facilitatorGroupId,@Param(value = "status")Integer status,@Param(value = "taskName")String taskName);

    List<UndistributedImcTaskDto> queryAllUndistributedTaskWithTime();

    List<ImcInspectionTask> queryAllUnDistributedTask(@Param(value = "facilitatorId")Long facilitatorId);

    List<ImcInspectionTask> queryAllFinishedTaskByFacilitatorId(@Param(value = "facilitatorId")Long facilitatorId);
}