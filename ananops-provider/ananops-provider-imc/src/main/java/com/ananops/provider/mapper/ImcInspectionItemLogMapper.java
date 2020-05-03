package com.ananops.provider.mapper;

import com.ananops.core.mybatis.MyMapper;
import com.ananops.provider.model.domain.ImcInspectionItemLog;
import com.ananops.provider.model.vo.ItemLogVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ImcInspectionItemLogMapper extends MyMapper<ImcInspectionItemLog> {
    List<ItemLogVo> queryItemLogListWithPage(ImcInspectionItemLog imcInspectionItemLog);
}