package com.ananops.provider.mapper;

import com.ananops.core.mybatis.MyMapper;
import com.ananops.provider.model.domain.RdcSceneDevice;
import com.ananops.provider.model.dto.RdcSceneDeviceWithCreator;
import com.ananops.provider.model.vo.RdcBindedDeviceVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by rongshuai on 2020/5/3 21:38
 */
@Mapper
public interface RdcSceneDeviceMapper extends MyMapper<RdcSceneDevice> {
    Integer deleteBindedDevice(@Param(value = "sceneId") Long sceneId,@Param(value = "deviceId") Long deviceId);

    RdcSceneDeviceWithCreator getSceneDevice(@Param(value = "deviceId") String deviceId);

    RdcSceneDevice getSceneCamera(@Param(value = "sceneId")Long sceneId);
}
