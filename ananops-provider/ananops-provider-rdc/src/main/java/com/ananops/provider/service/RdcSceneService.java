package com.ananops.provider.service;

import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.IService;
import com.ananops.provider.model.domain.RdcArrow;
import com.ananops.provider.model.domain.RdcScene;
import com.ananops.provider.model.domain.RdcSceneDevice;
import com.ananops.provider.model.dto.*;
import com.ananops.provider.model.dto.oss.OptUploadFileReqDto;
import com.ananops.provider.model.dto.oss.OptUploadFileRespDto;
import com.ananops.provider.model.vo.RdcArrowVo;
import com.ananops.provider.model.vo.RdcBindedDeviceVo;
import com.ananops.provider.model.vo.RdcSceneVo;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;

/**
 * Created by rongshuai on 2020/4/27 13:37
 */
public interface RdcSceneService extends IService<RdcScene> {
    RdcScene saveRdcScene(LoginAuthDto loginAuthDto,RdcAddSceneDto rdcAddSceneDto);

    List<OptUploadFileRespDto> uploadRdcScenePic(MultipartHttpServletRequest multipartRequest, OptUploadFileReqDto optUploadFileReqDto, LoginAuthDto loginAuthDto);

    List<RdcSceneVo> getAllRdcScene(LoginAuthDto loginAuthDto);

    RdcSceneDevice sceneBindDevice(RdcSceneDevice rdcSceneDevice);

    List<RdcBindedDeviceVo> getDeviceBySceneId(Long sceneId);

    void deleteBindedDevice(RdcSceneDeviceQueryDto rdcSceneDeviceQueryDto);

    RdcArrow createRdcArrow(LoginAuthDto loginAuthDto, RdcArrow rdcArrow);

    void deleteRdcArrow(Long arrowId);

    List<RdcArrowVo> getRdcArrowsBySceneId(Long sceneId);

    AlarmDeviceDto getAlarmDevice(DeviceDataDto deviceDataDto);

    double computeRadio(RdcSceneDeviceWithCreator rdcSceneDeviceWithCreator);

    void handleDeviceAlarm(String deviceId);
}
