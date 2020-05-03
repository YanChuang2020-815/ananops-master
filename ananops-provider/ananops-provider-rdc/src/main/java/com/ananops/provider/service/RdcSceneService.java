package com.ananops.provider.service;

import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.IService;
import com.ananops.provider.model.domain.RdcScene;
import com.ananops.provider.model.dto.RdcAddSceneDto;
import com.ananops.provider.model.dto.oss.OptUploadFileReqDto;
import com.ananops.provider.model.dto.oss.OptUploadFileRespDto;
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
}
