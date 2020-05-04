package com.ananops.provider.mapper;

import com.ananops.core.mybatis.MyMapper;
import com.ananops.provider.model.domain.RdcSceneDevice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by rongshuai on 2020/5/3 21:38
 */
@Mapper
public interface RdcSceneDeviceMapper extends MyMapper<RdcSceneDevice> {
    Integer deleteBindedDevice(@Param(value = "sceneId") Long sceneId,@Param(value = "deviceId") Long deviceId);
}
