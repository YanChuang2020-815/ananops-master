package com.ananops.provider.service.impl;

import com.ananops.PublicUtil;
import com.ananops.base.constant.GlobalConstant;
import com.ananops.base.dto.LoginAuthDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.base.exception.BusinessException;
import com.ananops.core.support.BaseService;
import com.ananops.provider.mapper.DeviceMapper;
import com.ananops.provider.mapper.RdcArrowMapper;
import com.ananops.provider.mapper.RdcSceneDeviceMapper;
import com.ananops.provider.mapper.RdcSceneMapper;
import com.ananops.provider.model.domain.Device;
import com.ananops.provider.model.domain.RdcArrow;
import com.ananops.provider.model.domain.RdcScene;
import com.ananops.provider.model.domain.RdcSceneDevice;
import com.ananops.provider.model.dto.*;
import com.ananops.provider.model.dto.attachment.OptAttachmentUpdateReqDto;
import com.ananops.provider.model.dto.attachment.OptUploadFileByteInfoReqDto;
import com.ananops.provider.model.dto.oss.ElementImgUrlDto;
import com.ananops.provider.model.dto.oss.OptBatchGetUrlRequest;
import com.ananops.provider.model.dto.oss.OptUploadFileReqDto;
import com.ananops.provider.model.dto.oss.OptUploadFileRespDto;
import com.ananops.provider.model.service.UacGroupFeignApi;
import com.ananops.provider.model.vo.RdcArrowVo;
import com.ananops.provider.model.vo.RdcBindedDeviceVo;
import com.ananops.provider.model.vo.RdcSceneVo;
import com.ananops.provider.service.OpcOssFeignApi;
import com.ananops.provider.service.RdcSceneService;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.xiaoleilu.hutool.io.FileTypeUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rongshuai on 2020/4/27 13:37
 */
@Service
public class RdcSceneServiceImpl extends BaseService<RdcScene> implements RdcSceneService {

    @Resource
    RdcSceneMapper rdcSceneMapper;

    @Resource
    UacGroupFeignApi uacGroupFeignApi;

    @Resource
    OpcOssFeignApi opcOssFeignApi;

    @Resource
    RdcSceneDeviceMapper rdcSceneDeviceMapper;

    @Resource
    DeviceMapper deviceMapper;

    @Resource
    RdcArrowMapper rdcArrowMapper;

    @Resource
    DefaultMQProducer defaultMQProducer;

    @Override
    public RdcScene saveRdcScene(LoginAuthDto loginAuthDto, RdcAddSceneDto rdcAddSceneDto){
        RdcScene rdcScene = new RdcScene();
        BeanUtils.copyProperties(rdcAddSceneDto,rdcScene);
        // 获取登录用户的组织Id
        Long userGroupId = loginAuthDto.getGroupId();
        try{
            // 根据组织Id查询公司Id
            Long groupId = uacGroupFeignApi.getCompanyInfoById(userGroupId).getResult().getId();
            rdcScene.setGroupId(groupId);
        }catch (Exception e){
            throw new BusinessException(ErrorCodeEnum.UAC10015001);
        }
        rdcScene.setUpdateInfo(loginAuthDto);
        if(rdcScene.isNew()){
            Long sceneId = super.generateId();
            rdcScene.setId(sceneId);
            List<String> attachmentIds = rdcAddSceneDto.getAttachmentIds();
            if(attachmentIds != null) {
                Long attachmentId = Long.valueOf(attachmentIds.get(0));
                String refNo = String.valueOf(super.generateId());
                rdcScene.setRefNo(refNo);
                logger.info("rdcScene={}",rdcScene);
                //为附件添加工单号
                OptAttachmentUpdateReqDto optAttachmentUpdateReqDto = new OptAttachmentUpdateReqDto();
                optAttachmentUpdateReqDto.setId(attachmentId);
                optAttachmentUpdateReqDto.setLoginAuthDto(loginAuthDto);
                optAttachmentUpdateReqDto.setRefNo(refNo);
                opcOssFeignApi.updateAttachmentInfo(optAttachmentUpdateReqDto);
            }else{
                throw new BusinessException(ErrorCodeEnum.GL99990100,rdcScene);
            }
            //新建场景
            rdcSceneMapper.insert(rdcScene);
        }else{
            try{
                List<String> attachmentIds = rdcAddSceneDto.getAttachmentIds();
                if(attachmentIds != null) {
                    Long attachmentId = Long.valueOf(attachmentIds.get(0));
                    String refNo = String.valueOf(super.generateId());
                    rdcScene.setRefNo(refNo);
                    //为附件添加工单号
                    OptAttachmentUpdateReqDto optAttachmentUpdateReqDto = new OptAttachmentUpdateReqDto();
                    optAttachmentUpdateReqDto.setId(attachmentId);
                    optAttachmentUpdateReqDto.setLoginAuthDto(loginAuthDto);
                    optAttachmentUpdateReqDto.setRefNo(refNo);
                    opcOssFeignApi.updateAttachmentInfo(optAttachmentUpdateReqDto);
                }else{
                    throw new BusinessException(ErrorCodeEnum.GL99990100,rdcScene);
                }
                //更新参数
                rdcSceneMapper.updateByPrimaryKeySelective(rdcScene);
            }catch (Exception e){
                throw new BusinessException(ErrorCodeEnum.RDC100000005,rdcScene);
            }
        }
        return rdcScene;
    }

    @Override
    public List<OptUploadFileRespDto> uploadRdcScenePic(MultipartHttpServletRequest multipartRequest, OptUploadFileReqDto optUploadFileReqDto, LoginAuthDto loginAuthDto){
        String filePath = optUploadFileReqDto.getFilePath();
        Long userId = loginAuthDto.getUserId();
        String userName = loginAuthDto.getUserName();
        List<OptUploadFileRespDto> result = Lists.newArrayList();
        try {
            List<MultipartFile> fileList = multipartRequest.getFiles("file");
            if (fileList.isEmpty()) {
                return result;
            }

            for (MultipartFile multipartFile : fileList) {
                String fileName = multipartFile.getOriginalFilename();
                if (PublicUtil.isEmpty(fileName)) {
                    continue;
                }
                Preconditions.checkArgument(multipartFile.getSize() <= GlobalConstant.FILE_MAX_SIZE, "上传文件不能大于5M");
                InputStream inputStream = multipartFile.getInputStream();

                String inputStreamFileType = FileTypeUtil.getType(inputStream);
                // 将上传文件的字节流封装到到Dto对象中
                OptUploadFileByteInfoReqDto optUploadFileByteInfoReqDto = new OptUploadFileByteInfoReqDto();
                optUploadFileByteInfoReqDto.setFileByteArray(multipartFile.getBytes());
                optUploadFileByteInfoReqDto.setFileName(fileName);
                optUploadFileByteInfoReqDto.setFileType(inputStreamFileType);
                optUploadFileReqDto.setUploadFileByteInfoReqDto(optUploadFileByteInfoReqDto);
                // 设置不同文件路径来区分图片
                optUploadFileReqDto.setFilePath("ananops/rdc/" + userId + "/" + filePath + "/");
                optUploadFileReqDto.setUserId(userId);
                optUploadFileReqDto.setUserName(userName);
                OptUploadFileRespDto optUploadFileRespDto = opcOssFeignApi.uploadFile(optUploadFileReqDto).getResult();
                result.add(optUploadFileRespDto);
            }
        } catch (IOException e) {
            logger.error("上传文件失败={}", e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<RdcSceneVo> getAllRdcScene(LoginAuthDto loginAuthDto){
        // 获取登录用户的组织Id
        Long userGroupId = loginAuthDto.getGroupId();
        Long groupId;
        try{
            // 根据组织Id查询公司Id
             groupId = uacGroupFeignApi.getCompanyInfoById(userGroupId).getResult().getId();
        }catch (Exception e){
            throw new BusinessException(ErrorCodeEnum.UAC10015001);
        }
        Example example = new Example(RdcScene.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("groupId",groupId);
        List<RdcScene> rdcSceneList = rdcSceneMapper.selectByExample(example);
        List<RdcSceneVo> rdcSceneVoList = new ArrayList<>();
        if(null!=rdcSceneList&&rdcSceneList.size()>0){
            rdcSceneList.forEach(rdcScene -> {
                rdcSceneVoList.add(transform(rdcScene));
            });
        }
        return rdcSceneVoList;
    }

    public RdcSceneVo transform(RdcScene rdcScene){
        RdcSceneVo rdcSceneVo = new RdcSceneVo();
        BeanUtils.copyProperties(rdcScene,rdcSceneVo);
        String refNo = rdcScene.getRefNo();
        OptBatchGetUrlRequest optBatchGetUrlRequest = new OptBatchGetUrlRequest();
        optBatchGetUrlRequest.setRefNo(refNo);
        optBatchGetUrlRequest.setEncrypt(true);
        List<ElementImgUrlDto> elementImgUrlDtoList = opcOssFeignApi.listFileUrl(optBatchGetUrlRequest).getResult();
        if(null!=elementImgUrlDtoList&&elementImgUrlDtoList.size()>0){
            rdcSceneVo.setUrl(elementImgUrlDtoList.get(0).getUrl());
        }
        return rdcSceneVo;
    }

    @Override
    public RdcSceneDevice sceneBindDevice(RdcSceneDevice rdcSceneDevice){
        rdcSceneDeviceMapper.insert(rdcSceneDevice);
        return rdcSceneDevice;
    }

    @Override
    public List<RdcBindedDeviceVo> getDeviceBySceneId(Long sceneId){
        List<RdcBindedDeviceVo> rdcBindedDeviceVoList = new ArrayList<>();
        Example example = new Example(RdcSceneDevice.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sceneId",sceneId);
        List<RdcSceneDevice> rdcSceneDeviceList = rdcSceneDeviceMapper.selectByExample(example);
        rdcSceneDeviceList.forEach(rdcSceneDevice -> {
            RdcBindedDeviceVo rdcBindedDeviceVo = new RdcBindedDeviceVo();
            BeanUtils.copyProperties(rdcSceneDevice,rdcBindedDeviceVo);
            Device device = deviceMapper.selectByPrimaryKey(rdcSceneDevice.getDeviceId());
            rdcBindedDeviceVo.setName(device.getName());
            String refNo = device.getRefNo();
            OptBatchGetUrlRequest optBatchGetUrlRequest = new OptBatchGetUrlRequest();
            optBatchGetUrlRequest.setRefNo(refNo);
            optBatchGetUrlRequest.setEncrypt(true);
            List<ElementImgUrlDto> elementImgUrlDtoList = opcOssFeignApi.listFileUrl(optBatchGetUrlRequest).getResult();
            if(null!=elementImgUrlDtoList&&elementImgUrlDtoList.size()>0){
                rdcBindedDeviceVo.setUrl(elementImgUrlDtoList.get(0).getUrl());
            }
            rdcBindedDeviceVoList.add(rdcBindedDeviceVo);
        });
        return rdcBindedDeviceVoList;
    }

    @Override
    public void deleteBindedDevice(RdcSceneDeviceQueryDto rdcSceneDeviceQueryDto){
        Long sceneId = rdcSceneDeviceQueryDto.getSceneId();
        Long deviceId = rdcSceneDeviceQueryDto.getDeviceId();
        Example example = new Example(RdcSceneDevice.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sceneId",sceneId);
        criteria.andEqualTo("deviceId",deviceId);
        try{
            rdcSceneDeviceMapper.deleteByExample(example);
        }catch (Exception e){
            throw new BusinessException(ErrorCodeEnum.GL9999098,rdcSceneDeviceQueryDto);
        }
    }

    @Override
    public RdcArrow createRdcArrow(LoginAuthDto loginAuthDto,RdcArrow rdcArrow){
        rdcArrow.setUpdateInfo(loginAuthDto);
        Long id = super.generateId();
        rdcArrow.setId(id);
        try{
            rdcArrowMapper.insert(rdcArrow);
        }catch (Exception e){
            throw new BusinessException(ErrorCodeEnum.GL99990100);
        }
        return rdcArrow;
    }

    @Override
    public void deleteRdcArrow(Long arrowId){
        try{
            rdcArrowMapper.deleteByPrimaryKey(arrowId);
        }catch (Exception e){
            throw new BusinessException(ErrorCodeEnum.GL99990100);
        }
    }

    @Override
    public List<RdcArrowVo> getRdcArrowsBySceneId(Long sceneId){
        List<RdcArrowQueryDto> rdcArrowQueryDtos = rdcArrowMapper.queryAllArrowBySceneId(sceneId);
        List<RdcArrowVo> rdcArrowVos = new ArrayList<>();
        if(null!=rdcArrowQueryDtos&&rdcArrowQueryDtos.size()>0){
            rdcArrowQueryDtos.forEach(rdcArrowQueryDto -> {
                RdcArrowVo rdcArrowVo = new RdcArrowVo();
                BeanUtils.copyProperties(rdcArrowQueryDto,rdcArrowVo);
                String refNo = rdcArrowQueryDto.getRefNo();
                OptBatchGetUrlRequest optBatchGetUrlRequest = new OptBatchGetUrlRequest();
                optBatchGetUrlRequest.setRefNo(refNo);
                optBatchGetUrlRequest.setEncrypt(true);
                List<ElementImgUrlDto> elementImgUrlDtoList = opcOssFeignApi.listFileUrl(optBatchGetUrlRequest).getResult();
                if(null!=elementImgUrlDtoList&&elementImgUrlDtoList.size()>0){
                    rdcArrowVo.setUrl(elementImgUrlDtoList.get(0).getUrl());
                }
                rdcArrowVos.add(rdcArrowVo);
            });
        }
        return rdcArrowVos;
    }

    @Override
    public AlarmDeviceDto getAlarmDevice(DeviceDataDto deviceDataDto){
        RdcSceneDeviceWithCreator rdcSceneDeviceWithCreator = rdcSceneDeviceMapper.getSceneDevice(deviceDataDto.getDeviceId());
        AlarmDeviceDto alarmDeviceDto = new AlarmDeviceDto();
        if(null != rdcSceneDeviceWithCreator){
            BeanUtils.copyProperties(rdcSceneDeviceWithCreator,alarmDeviceDto);
            alarmDeviceDto.setDeviceName(deviceDataDto.getName());
            if(deviceDataDto.getData()!=null&&deviceDataDto.getData().size()>0){
                alarmDeviceDto.setValue(deviceDataDto.getData().get(0).getValue());
            }
            return alarmDeviceDto;
        }else{
            return alarmDeviceDto;
        }
    }

    @Override
    public double computeRadio(RdcSceneDeviceWithCreator rdcSceneDeviceWithCreator) {
        Long sceneId = rdcSceneDeviceWithCreator.getSceneId();
        BigDecimal latTarget = rdcSceneDeviceWithCreator.getLatitude();
        BigDecimal lngTarget = rdcSceneDeviceWithCreator.getLongitude();
        RdcSceneDevice camera = rdcSceneDeviceMapper.getSceneCamera(sceneId);
        BigDecimal latOrigin = camera.getLatitude();
        BigDecimal lngOrigin = camera.getLongitude();
        double res =  Math.acos(
                Math.cos(latOrigin.doubleValue())*
                Math.cos(latTarget.doubleValue())*
                Math.cos(lngOrigin.doubleValue() - lngTarget.doubleValue())+
                Math.sin(latOrigin.doubleValue())*
                Math.sin(latTarget.doubleValue())
                )*180/Math.PI;
        if (lngTarget.doubleValue()>3.0) {
            res = -res;
        }
        AlarmDto alarmDto = new AlarmDto();
        alarmDto.setAngle(res);
        alarmDto.setDeviceId(rdcSceneDeviceWithCreator.getDeviceId());
        alarmDto.setSceneId(sceneId);
        this.alramPush(alarmDto);
        return res;
    }

    /**
     * 向rocketmq发送消息（报警设备转向角）
     * @param alarmDto
     */
    private void alramPush(AlarmDto alarmDto) {
        Message sendMsg = new Message("alarmToCamera",alarmDto.toString().getBytes());
        try {
            defaultMQProducer.send(sendMsg);
        } catch (Exception e){
            throw new BusinessException("向rocketmq发送消息异常");
        }
        System.out.println(alarmDto.toString());
    }
}
