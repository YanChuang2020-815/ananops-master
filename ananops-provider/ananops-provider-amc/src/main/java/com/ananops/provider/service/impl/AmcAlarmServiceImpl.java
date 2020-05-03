package com.ananops.provider.service.impl;

import com.ananops.PublicUtil;
import com.ananops.base.constant.GlobalConstant;
import com.ananops.base.dto.BaseQuery;
import com.ananops.base.dto.LoginAuthDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.base.exception.BusinessException;
import com.ananops.core.support.BaseService;
import com.ananops.core.utils.RequestUtil;
import com.ananops.provider.exception.AmcBizException;
import com.ananops.provider.manager.AmcAlarmManager;
import com.ananops.provider.mapper.AmcAlarmMapper;
import com.ananops.provider.model.domain.AmcAlarm;
import com.ananops.provider.model.domain.MqMessageData;
import com.ananops.provider.model.dto.AlarmQuery;
import com.ananops.provider.model.dto.attachment.OptAttachmentUpdateReqDto;
import com.ananops.provider.model.dto.attachment.OptUploadFileByteInfoReqDto;
import com.ananops.provider.model.dto.oss.ElementImgUrlDto;
import com.ananops.provider.model.dto.oss.OptBatchGetUrlRequest;
import com.ananops.provider.model.dto.oss.OptUploadFileReqDto;
import com.ananops.provider.model.dto.oss.OptUploadFileRespDto;
import com.ananops.provider.model.service.UacGroupFeignApi;
import com.ananops.provider.model.vo.GroupZtreeVo;
import com.ananops.provider.mq.producer.AlarmMsgProducer;
import com.ananops.provider.service.AmcAlarmService;
import com.ananops.provider.service.OpcOssFeignApi;
import com.ananops.wrapper.Wrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.xiaoleilu.hutool.io.FileTypeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By ChengHao On 2020/1/6
 */
@Slf4j
@Service
public class AmcAlarmServiceImpl extends BaseService<AmcAlarm> implements AmcAlarmService {
    @Resource
    AmcAlarmMapper amcAlarmMapper;
    @Resource
    UacGroupFeignApi uacGroupFeignApi;
    @Resource
    private OpcOssFeignApi opcOssFeignApi;
    @Resource
    AlarmMsgProducer alarmMsgProducer;
    @Resource
    AmcAlarmManager amcAlarmManager;

    @Override
    public int saveAlarm(AmcAlarm amcAlarm, LoginAuthDto loginAuthDto) {
        int result = 0;
        amcAlarm.setUpdateInfo(loginAuthDto);
        if (amcAlarm.isNew()) {
            amcAlarm.setId(super.generateId());
            amcAlarm.setAlarmStatus(1);
            amcAlarm.setGroupId(loginAuthDto.getGroupId());
            amcAlarm.setGroupName(loginAuthDto.getGroupName());
            result = amcAlarmMapper.insertSelective(amcAlarm);
            //推送mq消息
            MqMessageData mqMessageData = alarmMsgProducer.sendAlarmOccurMsgMq(amcAlarm);
            amcAlarmManager.alarmOccur(mqMessageData);
        } else {
            result = amcAlarmMapper.updateByPrimaryKeySelective(amcAlarm);
            if (result < 1) {
                throw new AmcBizException(ErrorCodeEnum.AMC10091011, amcAlarm.getId());
            }
        }
        // 更新附件信息
        List<Long> attachmentIds = new ArrayList<>();
        if (amcAlarm.getAlarmPhoto() != null) {
            String alarmPhotos = amcAlarm.getAlarmPhoto();
            if (alarmPhotos.contains(",")) {
                String[] legalIds = alarmPhotos.split(",");
                for (String id : legalIds) {
                    attachmentIds.add(Long.parseLong(id));
                }
            } else {
                attachmentIds.add(Long.parseLong(alarmPhotos));
            }
        }
        OptAttachmentUpdateReqDto optAttachmentUpdateReqDto = new OptAttachmentUpdateReqDto();
        optAttachmentUpdateReqDto.setAttachmentIds(attachmentIds);
        optAttachmentUpdateReqDto.setLoginAuthDto(loginAuthDto);
        optAttachmentUpdateReqDto.setRefNo(amcAlarm.getId().toString());
        opcOssFeignApi.batchUpdateAttachment(optAttachmentUpdateReqDto);
        return result;
    }

    @Override
    public AmcAlarm getAlarmById(Long id) {
        return amcAlarmMapper.selectByPrimaryKey(id);
    }

    @Override
    public PageInfo getAlarmListByGroupId(BaseQuery baseQuery) {
        PageHelper.startPage(baseQuery.getPageNum(), baseQuery.getPageSize());
        LoginAuthDto loginAuthDto = RequestUtil.getLoginUser();
        //通过组织ID查询组织列表
        Wrapper<List<GroupZtreeVo>> wrapper = uacGroupFeignApi.getGroupTreeById(loginAuthDto.getGroupId());
        Example example = new Example(AmcAlarm.class);
        //获取登录用户组织及其下属部分下的所有报警信息
        for (int i = 0; i < wrapper.getResult().size(); i++) {
            example.or(example.createCriteria().andEqualTo("groupId", wrapper.getResult().get(i).getId()));
            example.setOrderByClause("last_occur_time desc");
        }
        List<AmcAlarm> amcAlarmList = amcAlarmMapper.selectByExample(example);
        return new PageInfo<>(amcAlarmList);
    }

    @Override
    public PageInfo getAlarmListByAlarmLevel(AlarmQuery alarmQuery) {
        PageHelper.startPage(alarmQuery.getPageNum(), alarmQuery.getPageSize());
        LoginAuthDto loginAuthDto = RequestUtil.getLoginUser();
        Wrapper<List<GroupZtreeVo>> wrapper = uacGroupFeignApi.getGroupTreeById(loginAuthDto.getGroupId());
        Example example = new Example(AmcAlarm.class);
        List<Example.Criteria> list = new ArrayList<>();
        for (int i = 0; i < wrapper.getResult().size(); i++) {
            list.add(example.createCriteria());
            list.get(i).andEqualTo("groupId", wrapper.getResult().get(i).getId());
            list.get(i).andEqualTo("alarmLevel", alarmQuery.getAlarmLevel());
            example.or(list.get(i));
        }
        List<AmcAlarm> amcAlarmList = amcAlarmMapper.selectByExample(example);
        return new PageInfo<>(amcAlarmList);
    }

    @Override
    public PageInfo getAlarmListByAlarmStatus(AlarmQuery alarmQuery) {
        PageHelper.startPage(alarmQuery.getPageNum(), alarmQuery.getPageSize());
        LoginAuthDto loginAuthDto = RequestUtil.getLoginUser();
        Wrapper<List<GroupZtreeVo>> wrapper = uacGroupFeignApi.getGroupTreeById(loginAuthDto.getGroupId());
        Example example = new Example(AmcAlarm.class);
        List<Example.Criteria> list = new ArrayList<>();
        for (int i = 0; i < wrapper.getResult().size(); i++) {
            list.add(example.createCriteria());
            list.get(i).andEqualTo("groupId", wrapper.getResult().get(i).getId());
            list.get(i).andEqualTo("alarmStatus", alarmQuery.getAlarmStatus());
            example.or(list.get(i));
        }
        List<AmcAlarm> amcAlarmList = amcAlarmMapper.selectByExample(example);
        return new PageInfo<>(amcAlarmList);
    }

    @Override
    public int getAllAlarmCount() {
        LoginAuthDto loginAuthDto = RequestUtil.getLoginUser();
        Wrapper<List<GroupZtreeVo>> wrapper = uacGroupFeignApi.getGroupTreeById(loginAuthDto.getGroupId());
        Example example = new Example(AmcAlarm.class);
        for (int i = 0; i < wrapper.getResult().size(); i++) {
            example.or(example.createCriteria().andEqualTo("groupId", wrapper.getResult().get(i).getId()));
        }
        return amcAlarmMapper.selectCountByExample(example);
    }

    @Override
    public int getDealingCount() {
        LoginAuthDto loginAuthDto = RequestUtil.getLoginUser();
        Wrapper<List<GroupZtreeVo>> wrapper = uacGroupFeignApi.getGroupTreeById(loginAuthDto.getGroupId());
        Example example = new Example(AmcAlarm.class);
        List<Example.Criteria> list = new ArrayList<>();
        for (int i = 0; i < wrapper.getResult().size(); i++) {
            list.add(example.createCriteria());
            list.get(i).andEqualTo("groupId", wrapper.getResult().get(i).getId());
            list.get(i).andEqualTo("alarmStatus", 1);
            example.or(list.get(i));
        }
        return amcAlarmMapper.selectCountByExample(example);
    }

    @Override
    public int getUrgencyCount() {
        LoginAuthDto loginAuthDto = RequestUtil.getLoginUser();
        Wrapper<List<GroupZtreeVo>> wrapper = uacGroupFeignApi.getGroupTreeById(loginAuthDto.getGroupId());
        Example example = new Example(AmcAlarm.class);
        List<Example.Criteria> list = new ArrayList<>();
        for (int i = 0; i < wrapper.getResult().size(); i++) {
            list.add(example.createCriteria());
            list.get(i).andEqualTo("groupId", wrapper.getResult().get(i).getId());
            list.get(i).andEqualTo("alarmLevel", 1);
            example.or(list.get(i));
        }
        return amcAlarmMapper.selectCountByExample(example);
    }

    @Override
    public int getDealedCount() {
        LoginAuthDto loginAuthDto = RequestUtil.getLoginUser();
        Wrapper<List<GroupZtreeVo>> wrapper = uacGroupFeignApi.getGroupTreeById(loginAuthDto.getGroupId());
        Example example = new Example(AmcAlarm.class);
        List<Example.Criteria> list = new ArrayList<>();
        for (int i = 0; i < wrapper.getResult().size(); i++) {
            list.add(example.createCriteria());
            list.get(i).andEqualTo("groupId", wrapper.getResult().get(i).getId());
            list.get(i).andEqualTo("alarmStatus", 0);
            example.or(list.get(i));
        }
        return amcAlarmMapper.selectCountByExample(example);
    }

    @Override
    public int deleteAlarmByAlarmId(Long alarmId) {
        return amcAlarmMapper.deleteByPrimaryKey(alarmId);
    }

    @Override
    public int deleteAlarmsByAlarmStatus(int alarmStatus) {
        LoginAuthDto loginAuthDto = RequestUtil.getLoginUser();
        Wrapper<List<GroupZtreeVo>> wrapper = uacGroupFeignApi.getGroupTreeById(loginAuthDto.getGroupId());
        Example example = new Example(AmcAlarm.class);
        List<Example.Criteria> list = new ArrayList<>();
        for (int i = 0; i < wrapper.getResult().size(); i++) {
            list.add(example.createCriteria());
            list.get(i).andEqualTo("groupId", wrapper.getResult().get(i).getId());
            list.get(i).andEqualTo("alarmStatus", alarmStatus);
            example.or(list.get(i));
        }
        return amcAlarmMapper.deleteByExample(example);
    }

    @Override
    public List<OptUploadFileRespDto> uploadAlarmPhoto(MultipartHttpServletRequest multipartRequest, OptUploadFileReqDto optUploadFileReqDto, LoginAuthDto loginAuthDto) {
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
                optUploadFileReqDto.setFilePath("ananops/amc/" + userId + "/" + filePath + "/");
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
    public List<ElementImgUrlDto> getAlarmAttachment(Long id) {
        OptBatchGetUrlRequest optBatchGetUrlRequest = new OptBatchGetUrlRequest();
        optBatchGetUrlRequest.setRefNo(id.toString());
        optBatchGetUrlRequest.setEncrypt(true);
        optBatchGetUrlRequest.setExpires(null);
        return opcOssFeignApi.listFileUrl(optBatchGetUrlRequest).getResult();
    }

    @Override
    public int updateAlarmStatus(Long alarmId) {
        AmcAlarm amcAlarm = this.getAlarmById(alarmId);
        if (amcAlarm == null) {
            throw new BusinessException("无此报警信息");
        }
        Integer alarmStatus = amcAlarm.getAlarmStatus();
        alarmStatus = (alarmStatus == 1) ? 0 : 1;
        amcAlarm.setAlarmStatus(alarmStatus);
        amcAlarm.setUpdateInfo(RequestUtil.getLoginUser());
        return amcAlarmMapper.updateByPrimaryKeySelective(amcAlarm);
    }

}
