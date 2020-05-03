package com.ananops.provider.service;


import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.IService;
import com.ananops.provider.model.domain.ImcInspectionTaskLog;
import com.ananops.provider.model.dto.TaskLogQueryDto;
import com.ananops.provider.model.vo.TaskLogVo;

import java.util.List;

/**
 * Created by rongshuai on 2019/11/28 15:29
 */
public interface ImcInspectionTaskLogService extends IService<ImcInspectionTaskLog> {
    Integer createInspectionTaskLog(ImcInspectionTaskLog imcInspectionTaskLog, LoginAuthDto loginAuthDto);//创建一条巡检任务的日志

    List<TaskLogVo> getTaskLogsByTaskId(TaskLogQueryDto taskLogQueryDto);//根据巡检任务的id，查询所有的任务日志
}
