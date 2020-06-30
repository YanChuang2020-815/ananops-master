package com.ananops.provider.mapper;

import com.ananops.core.mybatis.MyMapper;
import com.ananops.provider.model.domain.Device;
import com.ananops.provider.model.vo.RdcDeviceVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeviceMapper extends MyMapper<Device> {
    List<Device> getAllDeviceByUserAndScene(@Param("groupId") Long groupId, @Param("sceneId") Long sceneId);
}