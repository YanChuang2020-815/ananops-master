package com.ananops.provider.service;

import com.alibaba.fastjson.JSONObject;
import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.IService;
import com.ananops.provider.model.domain.Device;
import com.ananops.provider.model.domain.RdcRule;
import com.ananops.provider.model.dto.DeviceDataDto;
import com.ananops.provider.model.dto.RdcAddDeviceDto;
import com.ananops.provider.model.vo.RdcDeviceVo;

import java.util.List;

public interface DeviceService extends IService<Device> {
    
    List<Device> getAllDevicesSelective(JSONObject json);
    
    Device getDeviceById(Long id);
    
    int addDevice(JSONObject json);
    
    int updateDevice(JSONObject json);

    Device createDevice(LoginAuthDto loginAuthDto, RdcAddDeviceDto rdcAddDeviceDto);

    List<RdcDeviceVo> getAllDevice(LoginAuthDto loginAuthDto);

    void deleteDevice(Long deviceId);

    List<RdcDeviceVo> getAllDeviceByUserAndScene(LoginAuthDto user, Long sceneId);

    void pushDeviceData(DeviceDataDto deviceDataDto);

    void deployRule(RdcRule rdcRule);

    RdcRule getRule(Long deviceId);
}
