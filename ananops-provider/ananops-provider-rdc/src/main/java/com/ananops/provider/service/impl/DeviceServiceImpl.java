package com.ananops.provider.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ananops.base.dto.LoginAuthDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.base.exception.BusinessException;
import com.ananops.core.support.BaseService;
import com.ananops.provider.mapper.DeviceMapper;
import com.ananops.provider.model.domain.Device;
import com.ananops.provider.model.dto.RdcAddDeviceDto;
import com.ananops.provider.model.dto.attachment.OptAttachmentUpdateReqDto;
import com.ananops.provider.model.dto.oss.ElementImgUrlDto;
import com.ananops.provider.model.dto.oss.OptBatchGetUrlRequest;
import com.ananops.provider.model.service.UacGroupFeignApi;
import com.ananops.provider.model.vo.RdcDeviceVo;
import com.ananops.provider.service.DeviceService;
import com.ananops.provider.service.OpcOssFeignApi;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class DeviceServiceImpl extends BaseService<Device> implements DeviceService {

    @Resource
    DeviceMapper deviceMapper;

    @Resource
    UacGroupFeignApi uacGroupFeignApi;

    @Resource
    OpcOssFeignApi opcOssFeignApi;

    public List<Device> getAllDevicesSelective(JSONObject json) {
        Device device = new Device();
        BeanUtils.copyProperties(json, device);
        return select(device);
    }
    
    public Device getDeviceById(Long id) {
        return selectByKey(id);
    }
    
    public int addDevice(JSONObject json) {
        Device device = new Device();
        BeanUtils.copyProperties(json, device);
        return save(device);
    }
    
    public int updateDevice(JSONObject json){
        Device device = new Device();
        BeanUtils.copyProperties(json, device);
        return update(device);
    }

    @Override
    public Device createDevice(LoginAuthDto loginAuthDto, RdcAddDeviceDto rdcAddDeviceDto){
        Device device = new Device();
        BeanUtils.copyProperties(rdcAddDeviceDto,device);
        // 获取登录用户的组织Id
        Long userGroupId = loginAuthDto.getGroupId();
        try{
            // 根据组织Id查询公司Id
            Long groupId = uacGroupFeignApi.getCompanyInfoById(userGroupId).getResult().getId();
            device.setGroupId(groupId);
        }catch (Exception e){
            throw new BusinessException(ErrorCodeEnum.UAC10015001);
        }
        device.setUpdateInfo(loginAuthDto);
        if(device.isNew()){
            Long deviceId = super.generateId();
            device.setId(deviceId);
            List<String> attachmentIds = rdcAddDeviceDto.getAttachmentIds();
            if(attachmentIds != null) {
                Long attachmentId = Long.valueOf(attachmentIds.get(0));
                String refNo = String.valueOf(super.generateId());
                device.setRefNo(refNo);
                logger.info("device={}",device);
                //为附件添加工单号
                OptAttachmentUpdateReqDto optAttachmentUpdateReqDto = new OptAttachmentUpdateReqDto();
                optAttachmentUpdateReqDto.setId(attachmentId);
                optAttachmentUpdateReqDto.setLoginAuthDto(loginAuthDto);
                optAttachmentUpdateReqDto.setRefNo(refNo);
                opcOssFeignApi.updateAttachmentInfo(optAttachmentUpdateReqDto);
            }else{
                throw new BusinessException(ErrorCodeEnum.GL99990100,device);
            }
            //新建设备
            deviceMapper.insert(device);
        }else{
            try{
                List<String> attachmentIds = rdcAddDeviceDto.getAttachmentIds();
                if(attachmentIds != null) {
                    Long attachmentId = Long.valueOf(attachmentIds.get(0));
                    String refNo = String.valueOf(super.generateId());
                    device.setRefNo(refNo);
                    //为附件添加工单号
                    OptAttachmentUpdateReqDto optAttachmentUpdateReqDto = new OptAttachmentUpdateReqDto();
                    optAttachmentUpdateReqDto.setId(attachmentId);
                    optAttachmentUpdateReqDto.setLoginAuthDto(loginAuthDto);
                    optAttachmentUpdateReqDto.setRefNo(refNo);
                    opcOssFeignApi.updateAttachmentInfo(optAttachmentUpdateReqDto);
                }else{
                    throw new BusinessException(ErrorCodeEnum.GL99990100,device);
                }
                //更新参数
                deviceMapper.updateByPrimaryKeySelective(device);
            }catch (Exception e){
                throw new BusinessException(ErrorCodeEnum.RDC100000005,device);
            }
        }
        return device;
    }

    @Override
    public List<RdcDeviceVo> getAllDevice(LoginAuthDto loginAuthDto){
        // 获取登录用户的组织Id
        Long userGroupId = loginAuthDto.getGroupId();
        Long groupId;
        try{
            // 根据组织Id查询公司Id
            groupId = uacGroupFeignApi.getCompanyInfoById(userGroupId).getResult().getId();
        }catch (Exception e){
            throw new BusinessException(ErrorCodeEnum.UAC10015001);
        }
        Example example = new Example(Device.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("groupId",groupId);
        List<Device> deviceList = deviceMapper.selectByExample(example);
        List<RdcDeviceVo> rdcDeviceVoList = new ArrayList<>();
        if(null!=deviceList&&deviceList.size()>0){
            deviceList.forEach(device -> {
                rdcDeviceVoList.add(transform(device));
            });
        }
        return rdcDeviceVoList;
    }

    private RdcDeviceVo transform(Device device){
        RdcDeviceVo rdcDeviceVo = new RdcDeviceVo();
        BeanUtils.copyProperties(device,rdcDeviceVo);
        String refNo = device.getRefNo();
        OptBatchGetUrlRequest optBatchGetUrlRequest = new OptBatchGetUrlRequest();
        optBatchGetUrlRequest.setRefNo(refNo);
        optBatchGetUrlRequest.setEncrypt(true);
        List<ElementImgUrlDto> elementImgUrlDtoList = opcOssFeignApi.listFileUrl(optBatchGetUrlRequest).getResult();
        if(null!=elementImgUrlDtoList&&elementImgUrlDtoList.size()>0){
            rdcDeviceVo.setUrl(elementImgUrlDtoList.get(0).getUrl());
        }
        return rdcDeviceVo;
    }
}
