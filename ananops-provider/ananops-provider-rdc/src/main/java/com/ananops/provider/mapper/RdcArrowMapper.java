package com.ananops.provider.mapper;

import com.ananops.core.mybatis.MyMapper;
import com.ananops.provider.model.domain.RdcArrow;
import com.ananops.provider.model.dto.RdcArrowQueryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RdcArrowMapper extends MyMapper<RdcArrow> {
    List<RdcArrowQueryDto> queryAllArrowBySceneId(@Param(value = "sceneId")Long sceneId);
}