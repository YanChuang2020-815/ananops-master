package com.ananops.provider.mapper;


import com.ananops.core.mybatis.MyMapper;
import com.ananops.provider.model.domain.ImcInspectionTaskLog;
import com.ananops.provider.model.vo.TaskLogVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ImcInspectionTaskLogMapper extends MyMapper<ImcInspectionTaskLog> {
    List<TaskLogVo> queryTaskLogListWithPage(ImcInspectionTaskLog imcInspectionTaskLog);
}