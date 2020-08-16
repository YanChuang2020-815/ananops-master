package com.ananops.provider.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ananops.base.dto.LoginAuthDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.base.exception.BusinessException;
import com.ananops.core.support.BaseService;
import com.ananops.provider.mapper.DeviceMapper;
import com.ananops.provider.mapper.RdcRuleMapper;
import com.ananops.provider.mapper.RdcSceneDeviceMapper;
import com.ananops.provider.model.device.EdgeDevice;
import com.ananops.provider.model.domain.Device;
import com.ananops.provider.model.domain.RdcRule;
import com.ananops.provider.model.domain.RdcSceneDevice;
import com.ananops.provider.model.dto.DeviceDataDto;
import com.ananops.provider.model.dto.RdcAddDeviceDto;
import com.ananops.provider.model.dto.RdcAddEdgeDeviceDto;
import com.ananops.provider.model.dto.attachment.OptAttachmentUpdateReqDto;
import com.ananops.provider.model.dto.oss.ElementImgUrlDto;
import com.ananops.provider.model.dto.oss.OptBatchGetUrlRequest;
import com.ananops.provider.model.service.UacGroupFeignApi;
import com.ananops.provider.model.vo.RdcDeviceVo;
import com.ananops.provider.service.DeviceService;
import com.ananops.provider.service.OpcOssFeignApi;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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

    @Resource
    RdcSceneDeviceMapper rdcSceneDeviceMapper;

    @Resource
    DefaultMQProducer defaultMQProducer;

    @Resource
    RdcRuleMapper rdcRuleMapper;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

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
        Example example = new Example(Device.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deviceId",rdcAddDeviceDto.getDeviceId());
        if(deviceMapper.selectCountByExample(example)>0){
            throw new BusinessException("设备已添加过了");
        }
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

    /**
     * 新建边缘设备（由云端托管）
     * @param loginAuthDto
     * @param rdcAddEdgeDeviceDto
     * @return
     */
    @Override
    public Device createEdgeDevice(LoginAuthDto loginAuthDto, RdcAddEdgeDeviceDto rdcAddEdgeDeviceDto) {
        Device device = new Device();
        BeanUtils.copyProperties(rdcAddEdgeDeviceDto,device);
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
        Example example = new Example(Device.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deviceId",rdcAddEdgeDeviceDto.getDeviceId());
        if(deviceMapper.selectCountByExample(example)>0){
            throw new BusinessException("设备已添加过了");
        }
        if(device.isNew()){
            Long deviceId = super.generateId();
            device.setId(deviceId);
            List<String> attachmentIds = rdcAddEdgeDeviceDto.getAttachmentIds();
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
            logger.info("uid is {}",rdcAddEdgeDeviceDto.getDeviceId());
            redisTemplate.opsForValue().set(rdcAddEdgeDeviceDto.getDeviceId(),String.valueOf(device.getId()));
            logger.info("redis中对应的设备Id为：{}",redisTemplate.opsForValue().get(rdcAddEdgeDeviceDto.getDeviceId()));
        }else{
            try{
                List<String> attachmentIds = rdcAddEdgeDeviceDto.getAttachmentIds();
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

    @Override
    public List<RdcDeviceVo> getAllDeviceByUserAndScene(LoginAuthDto user, Long sceneId) {
        // 获取登录用户的组织Id
        Long userGroupId = user.getGroupId();
        Long groupId;
        try{
            // 根据组织Id查询公司Id
            groupId = uacGroupFeignApi.getCompanyInfoById(userGroupId).getResult().getId();
        }catch (Exception e){
            throw new BusinessException(ErrorCodeEnum.UAC10015001);
        }
        List<Device> deviceList = deviceMapper.getAllDeviceByUserAndScene(groupId,sceneId);
        List<RdcDeviceVo> rdcDeviceVoList = new ArrayList<>();
        if(null!=deviceList&&deviceList.size()>0){
            deviceList.forEach(device -> {
                rdcDeviceVoList.add(transform(device));
            });
        }
        return rdcDeviceVoList;
    }

    @Override
    public void pushDeviceData(DeviceDataDto deviceDataDto) {
        Message msg = new Message("deviceData", JSONObject.toJSONString(deviceDataDto).getBytes());
        try {
            defaultMQProducer.send(msg);
        } catch (Exception e){
            throw new BusinessException("向rocketmq发送设备数据");
        }
        System.out.println(msg.toString());
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

    @Override
    public void deleteDevice(Long deviceId){
        try{
            Device device = deviceMapper.selectByPrimaryKey(deviceId);
            String uid = device.getDeviceId();
            deviceMapper.deleteByPrimaryKey(deviceId);
            Example example = new Example(RdcSceneDevice.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("deviceId",deviceId);
            rdcSceneDeviceMapper.deleteByExample(example);
            if (redisTemplate.hasKey(uid)) {
                redisTemplate.delete(uid);
            }
        }catch (Exception e){
            throw new BusinessException(ErrorCodeEnum.GL99990100);
        }
    }

    @Override
    public void deployRule(RdcRule rdcRule) {
        rdcRuleMapper.insert(rdcRule);
    }

    @Override
    public RdcRule getRule(Long deviceId) {
        Example example = new Example(RdcRule.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deviceAId",deviceId);
        return rdcRuleMapper.selectByExample(example).get(0);
    }
}
