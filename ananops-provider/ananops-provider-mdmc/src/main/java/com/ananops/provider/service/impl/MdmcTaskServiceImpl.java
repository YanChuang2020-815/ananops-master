package com.ananops.provider.service.impl;


import com.ananops.PublicUtil;
import com.ananops.base.constant.GlobalConstant;
import com.ananops.base.dto.LoginAuthDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.base.exception.BusinessException;
import com.ananops.core.support.BaseService;

import com.ananops.provider.manager.MdmcTaskManager;
import com.ananops.provider.mapper.*;
import com.ananops.provider.model.domain.*;
import com.ananops.provider.model.dto.*;
import com.ananops.provider.model.dto.attachment.OptAttachmentUpdateReqDto;
import com.ananops.provider.model.dto.attachment.OptUploadFileByteInfoReqDto;
import com.ananops.provider.model.dto.oss.*;
import com.ananops.provider.model.dto.user.UserInfoDto;
import com.ananops.provider.model.enums.*;
import com.ananops.provider.model.service.UacGroupBindUserFeignApi;
import com.ananops.provider.model.service.UacGroupFeignApi;
import com.ananops.provider.model.service.UacUserFeignApi;
import com.ananops.provider.model.vo.CompanyVo;
import com.ananops.provider.mq.producer.TaskMsgProducer;
import com.ananops.provider.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.xiaoleilu.hutool.io.FileTypeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
public class MdmcTaskServiceImpl extends BaseService<MdmcTask> implements MdmcTaskService {
    @Resource
    MdmcTaskMapper taskMapper;

    @Resource
    MdmcTaskItemMapper taskItemMapper;
    @Resource
    MdmcTaskLogMapper taskLogMapper;

    @Resource
    MdmcTaskItemService taskItemService;

    @Resource
    ImcItemFeignApi imcItemFeignApi;

    @Resource
    OpcOssFeignApi opcOssFeignApi;

    @Resource
    UacGroupBindUserFeignApi uacGroupBindUserFeignApi;

    @Resource
    MdmcFileTaskStatusMapper fileTaskStatusMapper;

    @Resource
    MdmcTroubleTypeMapper troubleTypeMapper;

    @Resource
    MdmcTroubleAddressMapper troubleAddressMapper;

    @Resource
    UacUserFeignApi uacUserFeignApi;

    @Resource
    SpcCompanyFeignApi spcCompanyFeignApi;

    @Resource
    SpcEngineerFeignApi spcEngineerFeignApi;

    @Resource
    PmcProjectFeignApi pmcProjectFeignApi;

    @Resource
    TaskMsgProducer taskMsgProducer;

    @Resource
    MdmcTaskManager taskManager;

    @Resource
    UacGroupFeignApi uacGroupFeignApi;

    @Override
    public MdmcTask getTaskByTaskId(Long taskId) {
        return taskMapper.selectByPrimaryKey(taskId);
    }

    @Override
    public MdmcTaskDetailDto getTaskDetail(Long taskId) {
        MdmcTaskDetailDto taskDetailDto=new MdmcTaskDetailDto();
        MdmcTask mdmcTask=taskMapper.selectByPrimaryKey(taskId);
        if (mdmcTask!=null){
        taskDetailDto.setMdmcTask(mdmcTask);
        if (mdmcTask.getUserId()!=null){
            UserInfoDto userInfoDto=uacUserFeignApi.getUacUserById(mdmcTask.getUserId()).getResult();
            taskDetailDto.setUserInfoDto(userInfoDto);
        }
        if (mdmcTask.getPrincipalId()!=null){
            UserInfoDto principalInfoDto=uacUserFeignApi.getUacUserById(mdmcTask.getPrincipalId()).getResult();
            taskDetailDto.setPrincipalInfoDto(principalInfoDto);
        }
        if (mdmcTask.getFacilitatorId()!=null){
            UserInfoDto companyVo=uacUserFeignApi.getUacUserById(mdmcTask.getFacilitatorId()).getResult();
            taskDetailDto.setCompanyVo(companyVo);
        }
        if (mdmcTask.getMaintainerId()!=null){
            UserInfoDto engineerDto=uacUserFeignApi.getUacUserById(mdmcTask.getMaintainerId()).getResult();
            taskDetailDto.setEngineerDto(engineerDto);
        }
        if (mdmcTask.getProjectId()!=null){
            PmcProjectDto pmcProjectDto=pmcProjectFeignApi.getProjectByProjectId(mdmcTask.getProjectId()).getResult();
            taskDetailDto.setPmcProjectDto(pmcProjectDto);
        }
        }
        else {
            throw new BusinessException(ErrorCodeEnum.MDMC9998098,taskId);
        }

        return taskDetailDto;
    }

    @Override
    public MdmcAddTaskDto saveTask(MdmcAddTaskDto mdmcAddTaskDto, LoginAuthDto loginAuthDto) {

        MdmcTask task = new MdmcTask();
        copyPropertiesWithIgnoreNullProperties(mdmcAddTaskDto,task);
        task.setUpdateInfo(loginAuthDto);
        List<Long> attachmentIdList=mdmcAddTaskDto.getAttachmentIdList();

        if(mdmcAddTaskDto.getId()==null){
            logger.info("创建一条维修工单记录... CrateTaskInfo = {}", mdmcAddTaskDto);

            Long taskId = super.generateId();
            task.setId(taskId);
            task.setStatus(2);
            taskMapper.insert(task);
            logger.info("新创建一条维修任务记录成功[OK], 创建维修任务子项中...");
            if (attachmentIdList != null && !attachmentIdList.isEmpty()){
                List<MdmcFileTaskStatus> fileTaskStatusList=taskMapper.selectByTaskIdAndStatus(taskId,2);
                if(fileTaskStatusList.size()>0){
                    for(MdmcFileTaskStatus file:fileTaskStatusList){
                        fileTaskStatusMapper.deleteByPrimaryKey(file);
                    }
                }
                Long refNo=super.generateId();
                for(Long attachmentId:attachmentIdList){
                    OptAttachmentUpdateReqDto optAttachmentUpdateReqDto=new OptAttachmentUpdateReqDto();
                    optAttachmentUpdateReqDto.setId(attachmentId);
                    optAttachmentUpdateReqDto.setLoginAuthDto(loginAuthDto);
                    optAttachmentUpdateReqDto.setRefNo(String.valueOf(refNo));
                    opcOssFeignApi.updateAttachmentInfo(optAttachmentUpdateReqDto);
                }

                MdmcFileTaskStatus fileTaskStatus = new MdmcFileTaskStatus();
                fileTaskStatus.setId(refNo);
                fileTaskStatus.setStatus(2);
                fileTaskStatus.setTaskId(taskId);
                fileTaskStatus.setUpdateInfo(loginAuthDto);
                fileTaskStatusMapper.insert(fileTaskStatus);

            }

            //获取所有的任务子项
            List<MdmcAddTaskItemDto> mdmcAddTaskItemDtoList = mdmcAddTaskDto.getMdmcAddTaskItemDtoList();
            if (mdmcAddTaskItemDtoList!=null){
                mdmcAddTaskItemDtoList.forEach(taskItem->{
                    //设置任务子项对应的任务id
                    taskItem.setTaskId(taskId);
                    //创建新的任务子项，并更新返回结果
                    MdmcTaskItem item = taskItemService.saveItem(taskItem,loginAuthDto);
                    BeanUtils.copyProperties(item ,taskItem);
                });
                BeanUtils.copyProperties(mdmcAddTaskItemDtoList,mdmcAddTaskDto);
            }

            //更新返回结果
            BeanUtils.copyProperties(task,mdmcAddTaskDto);

            logger.info("创建维修工单成功[OK] TaskDetail = {}", mdmcAddTaskDto);

        } else {
            logger.info("编辑/修改维修工单详情... UpdateInfo = {}", mdmcAddTaskDto);

            Long taskId = mdmcAddTaskDto.getId();
            MdmcTask t =taskMapper.selectByPrimaryKey(taskId);
            if (t == null) {//如果没有此任务
                throw new BusinessException(ErrorCodeEnum.MDMC9998098,taskId);
            }

            // 更新工单信息和状态
            taskMapper.updateByPrimaryKeySelective(task);

            MdmcTask task1=taskMapper.selectByPrimaryKey(taskId);
            BeanUtils.copyProperties(task1,task);
            Integer status = task.getStatus();
            Integer objectType = task.getObjectType();

            if (attachmentIdList!=null && !attachmentIdList.isEmpty()){
                List<MdmcFileTaskStatus> fileTaskStatusList1=taskMapper.selectByTaskIdAndStatus(taskId,status);
                if(fileTaskStatusList1.size()>0){
                    for(MdmcFileTaskStatus file:fileTaskStatusList1){
                        fileTaskStatusMapper.deleteByPrimaryKey(file);
                    }
                }
                Long refNo1=super.generateId();

                for (Long attachmentId:attachmentIdList){
                    OptAttachmentUpdateReqDto attachmentUpdateReqDto=new OptAttachmentUpdateReqDto();
                    attachmentUpdateReqDto.setId(attachmentId);
                    attachmentUpdateReqDto.setLoginAuthDto(loginAuthDto);
                    attachmentUpdateReqDto.setRefNo(String.valueOf(refNo1));
                    opcOssFeignApi.updateAttachmentInfo(attachmentUpdateReqDto);
                }

                MdmcFileTaskStatus mdmcFileTaskStatus = new MdmcFileTaskStatus();
                mdmcFileTaskStatus.setId(refNo1);
                mdmcFileTaskStatus.setStatus(task.getStatus());
                mdmcFileTaskStatus.setTaskId(taskId);
                mdmcFileTaskStatus.setUpdateInfo(loginAuthDto);
                fileTaskStatusMapper.insert(mdmcFileTaskStatus);
            }

            // 维修工单完成(无需评价)时，如果是巡检发起的维修任务，更新巡检工单状态
            if(status == MdmcTaskStatusEnum.DaiPingJia.getStatusNum() && objectType == MdmcObjectTypeEnum.IMC.getCode()) {
                Long imcItemId = task.getObjectId();
                if(imcItemId != null) {

                    ImcItemChangeStatusDto imcItemChangeStatusDto = new ImcItemChangeStatusDto();
                    imcItemChangeStatusDto.setLoginAuthDto(loginAuthDto);
                    imcItemChangeStatusDto.setStatus(4);
                    imcItemChangeStatusDto.setItemId(imcItemId);
                    imcItemFeignApi.modifyImcItemStatus(imcItemChangeStatusDto);
                    logger.info("用户负责人支付完成，更新巡检状态");

                }
            }

            // 更新返回结果
            BeanUtils.copyProperties(task,mdmcAddTaskDto);

            logger.info("编辑/修改维修工单成功[OK] Task = {}", task);

        }

        MdmcTaskLog taskLog=new MdmcTaskLog();
        Long taskLogId = super.generateId();
        taskLog.setUpdateInfo(loginAuthDto);
        taskLog.setId(taskLogId);
        taskLog.setTaskId(task.getId());
        taskLog.setStatus(task.getStatus());

        String move=MdmcTaskStatusEnum.getStatusMsg(task.getStatus());
        if(task.getLevel()!=null){
            move+=("，当前紧急程度是"+task.getLevel());
        }
        taskLog.setMovement(move);
        taskLogMapper.insert(taskLog);

        logger.info("记录维修工单操作日志[OK] TaskLog = {}", taskLog);
       if (task.getStatus()!=null&&task.getStatus()!=4 &&task.getStatus()!=14&&task.getStatus()!=15){
           MqMessageData mqMessageData = taskMsgProducer.sendTaskStatusMsgMq(task);
           taskManager.modifyTaskStatus(mqMessageData);
       }

        return mdmcAddTaskDto;
    }

    @Override
    public List<MdmcUserWatcherDto> getUserWatcherList(Long userId) {
        List<MdmcUserWatcherDto> userWatcherDtoList=new ArrayList<>();
        MdmcUserWatcherDto userWatcherDto=new MdmcUserWatcherDto();
        userWatcherDto.setUserId(userId);
        UserInfoDto userInfoDto=uacUserFeignApi.getUacUserById(userId).getResult();
        if (userInfoDto!=null && userInfoDto.getUserName()!=null){
            userWatcherDto.setUserName(userInfoDto.getUserName());
        }
        userWatcherDtoList.add(userWatcherDto);
        if (userInfoDto!=null && userInfoDto.getGroupId()!=null){
            List<Long> idList=uacGroupFeignApi.getUacUserIdListByGroupId(userInfoDto.getGroupId()).getResult();
            if (idList.size()>0){
                for (Long id:idList){
                    UserInfoDto userInfoDto1=uacUserFeignApi.getUacUserById(id).getResult();
                    if (userInfoDto1!=null&& userInfoDto1.getRoleCode()!=null && userInfoDto1.getRoleCode().equals("user_watcher")){
                        MdmcUserWatcherDto userWatcherDto2=new MdmcUserWatcherDto();
                        userWatcherDto2.setUserId(id);

                        if (userInfoDto1.getUserName()!=null){
                            userWatcherDto2.setUserName(userInfoDto1.getUserName());
                        }
                        userWatcherDtoList.add(userWatcherDto2);
                    }

                }
            }
        }
        if (userWatcherDtoList.size()>1){
            userWatcherDtoList.remove(0);
        }
        return userWatcherDtoList;
    }

    @Override
    public MdmcAddTroubleInfoDto saveTroubleList(MdmcAddTroubleInfoDto addTroubleInfoDto, LoginAuthDto loginAuthDto) {
        Long userId=addTroubleInfoDto.getUserId();
        Long groupId=uacGroupBindUserFeignApi.getCompanyGroupIdByUserId(userId).getResult();

        if (groupId==null){
            throw new BusinessException(ErrorCodeEnum.UAC10015010,groupId);
        }
        List<String> troubleTypeList=addTroubleInfoDto.getTroubleTypeList();
        List<MdmcTroubleAddressDto> troubleAddrssList=addTroubleInfoDto.getTroubleAddressList();
        if (troubleAddrssList.isEmpty() && troubleTypeList.isEmpty()){
            throw  new BusinessException(ErrorCodeEnum.MDMC99980005);
        }


        if (troubleTypeList.size()>0){
            for (String troubleType:troubleTypeList){
                if(troubleType.length()>0){
                    MdmcTroubleType mdmcTroubleType=new MdmcTroubleType();
                    Long typeId=super.generateId();
                    mdmcTroubleType.setId(typeId);
                    mdmcTroubleType.setGroupId(groupId);
                    mdmcTroubleType.setTroubleType(troubleType);
                    mdmcTroubleType.setUpdateInfo(loginAuthDto);
                    troubleTypeMapper.insert(mdmcTroubleType);
                }
            }
        }
        if (troubleAddrssList.size()>0){
            for (MdmcTroubleAddressDto troubleAddressDto:troubleAddrssList){
                if (troubleAddressDto!=null && (troubleAddressDto.getTroubleLatitude()!=null
                        ||troubleAddressDto.getTroubleAddress()!=null)){

                    MdmcTroubleAddress mdmcTroubleAddress=new MdmcTroubleAddress();
                    Long addressId=super.generateId();
                    mdmcTroubleAddress.setId(addressId);
                    mdmcTroubleAddress.setGroupId(groupId);
                    copyPropertiesWithIgnoreNullProperties(troubleAddressDto,mdmcTroubleAddress);
                    mdmcTroubleAddress.setUpdateInfo(loginAuthDto);
                    troubleAddressMapper.insert(mdmcTroubleAddress);
                }

            }
        }

        return addTroubleInfoDto;
    }

    @Override
    public MdmcAddTroubleInfoDto getTroubleList(Long userId, LoginAuthDto loginAuthDto) {
        if (userId == null) {
            userId = loginAuthDto.getUserId();
        }
        // 获取该用户所属的公司ID
        Long groupId = uacGroupBindUserFeignApi.getCompanyGroupIdByUserId(userId).getResult();
        // 查询系统所需字典项内容

        MdmcAddTroubleInfoDto mdmcAddTroubleInfoDto=new MdmcAddTroubleInfoDto();
        mdmcAddTroubleInfoDto.setUserId(userId);
        Example example=new Example(MdmcTroubleAddress.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("groupId",groupId);
        List<MdmcTroubleAddressDto> troubleAddressDtoList=new ArrayList<>();
        List<MdmcTroubleAddress> troubleAddressList=troubleAddressMapper.selectByExample(example);
        if (troubleAddressList!=null){
            for (MdmcTroubleAddress troubleAddress:troubleAddressList){
                MdmcTroubleAddressDto troubleAddressDto=new MdmcTroubleAddressDto();
                BeanUtils.copyProperties(troubleAddress,troubleAddressDto);
                troubleAddressDtoList.add(troubleAddressDto);
            }
        }
        else {
            List<MdmcTroubleAddress> troubleAddressList1=troubleAddressMapper.selectAll();
            for (int i=0;i<5;i++){
                MdmcTroubleAddressDto troubleAddressDto1=new MdmcTroubleAddressDto();
                if(troubleAddressList1!=null){
                    MdmcTroubleAddress troubleAddress1=troubleAddressList1.get(i);
                    BeanUtils.copyProperties(troubleAddress1,troubleAddressDto1);
                    troubleAddressDtoList.add(troubleAddressDto1);
                }
            }
        }

        mdmcAddTroubleInfoDto.setTroubleAddressList(troubleAddressDtoList);
        Example example1=new Example(MdmcTroubleType.class);
        Example.Criteria criteria1=example1.createCriteria();
        criteria1.andEqualTo("groupId",groupId);
        List<String> troubleTypeList=new ArrayList<>();
        List<MdmcTroubleType> mdmcTroubleTypeList=troubleTypeMapper.selectByExample(example1);
        if (mdmcTroubleTypeList!=null){
            for (MdmcTroubleType troubleType:mdmcTroubleTypeList){
                troubleTypeList.add(troubleType.getTroubleType());
            }
        }
        else{
            List<MdmcTroubleType> troubleTypes=troubleTypeMapper.selectAll();
            for (int i=0;i<3;i++){
                if (troubleTypes!=null){
                    troubleTypeList.add(troubleTypes.get(i).getTroubleType());
                }
            }
        }
        mdmcAddTroubleInfoDto.setTroubleTypeList(troubleTypeList);
        return mdmcAddTroubleInfoDto;

    }

    @Override
    public MdmcTaskDto modifyTask(MdmcTaskDto mdmcTaskDto) {
        logger.info("修改维修工单详情... UpdateInfo = {}", mdmcTaskDto);

        MdmcTask task = new MdmcTask();
        copyPropertiesWithIgnoreNullProperties(mdmcTaskDto,task);
        Long taskId = mdmcTaskDto.getId();
        if (taskId==null){
            throw new BusinessException(ErrorCodeEnum.MDMC9998098,taskId);
        }
        Example example = new Example(MdmcTask.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",taskId);
        List<MdmcTask> taskList =taskMapper.selectByExample(example);
        if(taskList.size()==0){//如果没有此任务
            throw new BusinessException(ErrorCodeEnum.MDMC9998098,taskId);
        }
        //如果当前是更新一条记录
//            String key = RedisKeyUtil.createMqKey(topic,tag,String.valueOf(task.getId()),body);
//            mqMessageData = new MqMessageData(body, topic, tag, key);
//            taskManager.saveTask(mqMessageData,task,false);
        taskMapper.updateByPrimaryKeySelective(task);
        //更新返回结果
        BeanUtils.copyProperties(task,mdmcTaskDto);

        logger.info("修改维修工单详情成功[OK] Task = {}", task);
        if (task.getStatus()!=null&&task.getStatus()!=4 &&task.getStatus()!=14&&task.getStatus()!=15) {
            MqMessageData mqMessageData = taskMsgProducer.sendTaskStatusMsgMq(task);
            taskManager.modifyTaskStatus(mqMessageData);
        }
        return mdmcTaskDto;
    }


    @Override
    public MdmcTask modifyTaskStatus(MdmcChangeStatusDto changeStatusDto,LoginAuthDto loginAuthDto) {
        logger.info("更新维修工单状态... UpdateInfo = {}", changeStatusDto);

        if(loginAuthDto == null) {
            logger.error("警告：匿名用户正在操作！");
        }

        Long taskId = changeStatusDto.getTaskId();
        MdmcTask task = getTaskByTaskId(taskId);
        if(task == null){//如果当前任务不存在
            throw new BusinessException(ErrorCodeEnum.MDMC9998098,taskId);
        }

        Integer status = changeStatusDto.getStatus();
        if (status == MdmcTaskStatusEnum.QuXiao.getStatusNum()){          // 工单终止
            logger.info("当前维修工单已被终止[Terminal] Task = {}", task);
            Example example = new Example(MdmcTaskItem.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("taskId",taskId);
            taskItemMapper.deleteByPrimaryKey(example);
            taskMapper.deleteByPrimaryKey(taskId);
        } else {
            task.setStatus(status);
            task.setUpdateInfo(loginAuthDto);
//        // 可靠服务
//        MqMessageData mqMessageData;
//        String body = JSON.toJSONString(changeStatusDto);
//        String topic = AliyunMqTopicConstants.MqTagEnum.MODIFY_INSPECTION_TASK_STATUS.getTopic();
//        String tag = AliyunMqTopicConstants.MqTagEnum.MODIFY_INSPECTION_TASK_STATUS.getTag();
//        String key = RedisKeyUtil.createMqKey(topic,tag,String.valueOf(task.getId()),body);
//        mqMessageData = new MqMessageData(body, topic, tag, key);
//        taskManager.modifyTaskStatus(mqMessageData,task);
            taskMapper.updateByPrimaryKey(task);

            // 如果是巡检发起的维修任务，更新巡检工单状态
            Integer objectType = task.getObjectType();
            if (status == MdmcTaskStatusEnum.WanCheng.getStatusNum() && objectType == MdmcObjectTypeEnum.IMC.getCode()) {
                Long imcItemId = task.getObjectId();
                if (imcItemId != null) {

                    ImcItemChangeStatusDto imcItemChangeStatusDto = new ImcItemChangeStatusDto();
                    imcItemChangeStatusDto.setLoginAuthDto(loginAuthDto);
                    imcItemChangeStatusDto.setStatus(4);
                    imcItemChangeStatusDto.setItemId(imcItemId);
                    imcItemFeignApi.modifyImcItemStatus(imcItemChangeStatusDto);
                    logger.info("维修工单完成，更新巡检状态");

                }
            }

            logger.info("更新维修工单状态成功[OK] Task = {}", task);
        }

        // 记录维修工单状态更新
        MdmcTaskLog taskLog=new MdmcTaskLog();
        Long taskLogId = super.generateId();
        taskLog.setUpdateInfo(loginAuthDto);
        taskLog.setId(taskLogId);
        taskLog.setTaskId(taskId);
        taskLog.setStatus(status);
        String move=MdmcTaskStatusEnum.getStatusMsg(task.getStatus());
        if(task.getLevel()!=null){
            move+=("，当前紧急程度是"+task.getLevel());
        }
        taskLog.setMovement(move);
        taskLogMapper.insert(taskLog);

        logger.info("记录维修工单操作日志[OK] TaskLog = {}", taskLog);
        if (task.getStatus()!=null&&task.getStatus()!=4 &&task.getStatus()!=14&&task.getStatus()!=15){
            MqMessageData mqMessageData = taskMsgProducer.sendTaskStatusMsgMq(task);
            taskManager.modifyTaskStatus(mqMessageData);
        }
        return task;
    }

    @Override
    public List<MdmcTask> getTaskListByStatus(MdmcStatusDto statusDto) {
        Example example = new Example(MdmcTask.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status",statusDto.getStatus());
        if(taskMapper.selectCountByExample(example)==0){
            throw new BusinessException(ErrorCodeEnum.MDMC9998098);
        }
        PageHelper.startPage(statusDto.getPageNum(),statusDto.getPageSize());
        return taskMapper.selectByExample(example);
    }

    @Override
    public List<MdmcTask> getTaskList(MdmcStatusDto statusDto) {

        PageHelper.startPage(statusDto.getPageNum(),statusDto.getPageSize());
        return taskMapper.selectAll();
    }

    @Override
    public List<MdmcTask> getTaskListByIdAndStatus(MdmcQueryDto queryDto) {
        // todo 分页查询
//        String roleCode=queryDto.getRoleCode();
        Long id=queryDto.getId();
        Integer status=queryDto.getStatus();
//        Example example = new Example(MdmcTask.class);
//        Example.Criteria criteria = example.createCriteria();
//        if (status!=null){
//        criteria.andEqualTo("status",queryDto.getStatus());
//        }
//
//        switch (roleCode){
//            case "user_watcher":criteria.andEqualTo("userId",id);break;
//            case "user_manager":criteria.andEqualTo("principalId",id);break;
//            case "engineer":criteria.andEqualTo("maintainerId",id);break;
//            case "fac_service":criteria.andEqualTo("facilitatorId",id);break;
//            case "fac_manager":criteria.andEqualTo("facilitatorId",id);break;
//            default: throw new BusinessException(ErrorCodeEnum.UAC10012008,roleCode);
//        }
//        if(taskMapper.selectCountByExample(example)==0){
//            throw new BusinessException(ErrorCodeEnum.MDMC9998098);
//        }
//        PageHelper.startPage(queryDto.getPageNum(),queryDto.getPageSize());

//        return taskMapper.selectByExample(example);

        return getTaskListByUserIdAndStatusOptional(id, status);
    }

    @Override
    public List<MdmcListDto> getTaskListByIdAndStatusArrary(MdmcStatusArrayDto statusArrayDto) {
//        String roleCode=statusArrayDto.getRoleCode();
        Long id=statusArrayDto.getId();

        Integer[] status=statusArrayDto.getStatus();
//        Example example = new Example(MdmcTask.class);
//        Example.Criteria criteria = example.createCriteria();
        List<MdmcListDto> listDtoList=new ArrayList<>();

//        switch (roleCode){
//            case "user_watcher":criteria.andEqualTo("userId",id);break;
//            case "user_manager":criteria.andEqualTo("principalId",id);break;
//            case "engineer":criteria.andEqualTo("maintainerId",id);break;
//            case "fac_service":criteria.andEqualTo("facilitatorId",id);break;
//            case "fac_manager":criteria.andEqualTo("facilitatorId",id);break;
//            default: throw new BusinessException(ErrorCodeEnum.UAC10012008,roleCode);
//        }
        if (status!=null){
            for (Integer i:status){
//                Example example1 = new Example(MdmcTask.class);
//                Example.Criteria criteria1 = example1.createCriteria();
//                criteria1.andEqualTo("status",i);
//                switch (roleCode){
//                    case "user_watcher":criteria1.andEqualTo("userId",id);break;
//                    case "user_manager":criteria1.andEqualTo("principalId",id);break;
//                    case "engineer":criteria1.andEqualTo("maintainerId",id);break;
//                    case "fac_service":criteria1.andEqualTo("facilitatorId",id);break;
//                    case "fac_manager":criteria.andEqualTo("facilitatorId",id);break;
//                    default: throw new BusinessException(ErrorCodeEnum.UAC10012008,roleCode);
//                }
                MdmcListDto listDto=new MdmcListDto();
                listDto.setStatus(i);
//                listDto.setTaskList(taskMapper.selectByExample(example1));
                List<MdmcTask> taskList=getTaskListByUserIdAndStatusOptional(id, i);
                List<MdmcTaskListDto> taskListDtoList=new ArrayList<>();
                if(taskList!=null){
                    for (MdmcTask task:taskList){
                        MdmcTaskListDto mdmcTaskListDto=new MdmcTaskListDto();
                        mdmcTaskListDto.setMdmcTask(task);
                        if (id!=null){
                            mdmcTaskListDto.setUserInfoDto(uacUserFeignApi.getUacUserById(id).getResult());
                        }
                        if (task.getProjectId()!=null){
                            mdmcTaskListDto.setPmcProjectDto(pmcProjectFeignApi.getProjectByProjectId(task.getProjectId()).getResult());
                        }
                       taskListDtoList.add(mdmcTaskListDto);
                    }
                }
                listDto.setTaskList(taskListDtoList);
                listDtoList.add(listDto);
            }

        } else{
            MdmcListDto listDto1=new MdmcListDto();
//            listDto1.setTaskList(taskMapper.selectByExample(example));
            List<MdmcTask> taskList1=getTaskListByUserIdAndStatusOptional(id, null);
            List<MdmcTaskListDto> taskListDtoList1=new ArrayList<>();
            if(taskList1!=null){
                for (MdmcTask task:taskList1){
                    MdmcTaskListDto mdmcTaskListDto1=new MdmcTaskListDto();
                    mdmcTaskListDto1.setMdmcTask(task);
                    if (id!=null){
                        mdmcTaskListDto1.setUserInfoDto(uacUserFeignApi.getUacUserById(id).getResult());
                    }
                    if (task.getProjectId()!=null){
                        mdmcTaskListDto1.setPmcProjectDto(pmcProjectFeignApi.getProjectByProjectId(task.getProjectId()).getResult());
                    }
                    taskListDtoList1.add(mdmcTaskListDto1);
                }
            }
            listDto1.setTaskList(taskListDtoList1);
            listDtoList.add(listDto1);
        }

        PageHelper.startPage(statusArrayDto.getPageNum(),statusArrayDto.getPageSize());
        return listDtoList;
    }

    @Override
    public PageInfo getTaskListByPage(MdmcQueryDto queryDto) {
//        String roleCode=queryDto.getRoleCode();
        Long id=queryDto.getId();
//        Example example = new Example(MdmcTask.class);
//        Example.Criteria criteria = example.createCriteria();
//        if (status!=null){
//            criteria.andEqualTo("status",queryDto.getStatus());
//        }
//        switch (roleCode){
//            case "user_watcher":criteria.andEqualTo("userId",id);break;
//            case "user_manager":criteria.andEqualTo("principalId",id);break;
//            case "engineer":criteria.andEqualTo("maintainerId",id);break;
//            case "fac_service":criteria.andEqualTo("facilitatorId",id);break;
//            case "fac_manager":criteria.andEqualTo("facilitatorId",id);break;
//            default: throw new BusinessException(ErrorCodeEnum.UAC10012008,roleCode);
//        }
//        if(taskMapper.selectCountByExample(example)==0){
//            throw new BusinessException(ErrorCodeEnum.MDMC9998098);
//        }


//        pageDto.setTaskList(taskMapper.selectByExample(example));
        List<MdmcTask> taskList=getTaskListOptional(queryDto,id);


        return new PageInfo<>(taskList);

    }

    @Override
    public int getTaskCount(Long userId) {

        return taskMapper.selectBySomeoneId(userId).size();
    }

    private List<MdmcTask> getTaskListOptional(MdmcQueryDto queryDto,Long userId){
        String roleCode="";
        if (userId!=null)roleCode=uacUserFeignApi.getUacUserById(userId).getResult().getRoleCode();
        Integer status=queryDto.getStatus();
        if(status == null) {

            if (roleCode!=null){
                if (roleCode.equals("fac_service")||roleCode.equals("fac_manager")||roleCode.equals("fac_leader")){
                    PageHelper.startPage(queryDto.getPageNum(),queryDto.getPageSize());
                    List<MdmcTask> factaskList=taskMapper.selectByFacId(userId);
                    return factaskList;
                }
                if(roleCode.equals("engineer")){
                    PageHelper.startPage(queryDto.getPageNum(),queryDto.getPageSize());
                    List<MdmcTask> mantainerTaskList=taskMapper.selectByMantainerId(userId);
                    return  mantainerTaskList;
                }
                if(roleCode.equals("user_watcher") || roleCode.equals("user")||roleCode.equals("user_leader")){
                    PageHelper.startPage(queryDto.getPageNum(),queryDto.getPageSize());
                    List<MdmcTask> taskList=taskMapper.selectBySomeoneId(userId);
                    return  taskList;
                }
            }
            else
            {PageHelper.startPage(queryDto.getPageNum(),queryDto.getPageSize());
                return taskMapper.selectBySomeoneId(userId);}
        } else{
            PageHelper.startPage(queryDto.getPageNum(),queryDto.getPageSize());
            return taskMapper.selectBySomeoneIdAndStatus(userId, status);
        }

        return taskMapper.selectBySomeoneIdAndStatus(userId, status);
    }

    private List<MdmcTask> getTaskListByUserIdAndStatusOptional(Long userId, Integer status) {
        String roleCode="";
        if (userId!=null)roleCode=uacUserFeignApi.getUacUserById(userId).getResult().getRoleCode();
        if(status == null) {

            List<MdmcTask> taskList=taskMapper.selectBySomeoneId(userId);
            List<MdmcTask> taskList1=new ArrayList<>();
            if (roleCode!=null){
                if (roleCode.equals("fac_service")||roleCode.equals("fac_manager")||roleCode.equals("fac_leader")){
                    for (MdmcTask task:taskList){
                        if (task.getStatus()>2)
                            taskList1.add(task);
                    }
                    return taskList1;
                }
                if(roleCode.equals("engineer")){
                    for (MdmcTask task:taskList){
                        if (task.getStatus()>4 && task.getStatus()!=14)
                            taskList1.add(task);
                    }
                    return  taskList1;
                }
                if(roleCode.equals("user_watcher") || roleCode.equals("user")||roleCode.equals("user_leader")){
                    taskList1.addAll(taskList);
                    return  taskList1;
                }
            }
            else return taskList;



        } else
            return taskMapper.selectBySomeoneIdAndStatus(userId, status);
        return taskMapper.selectBySomeoneIdAndStatus(userId, status);

    }


    @Override
    public MdmcTask modifyMaintainer(MdmcChangeMaintainerDto mdmcChangeMaintainerDto){
        LoginAuthDto loginAuthDto = mdmcChangeMaintainerDto.getLoginAuthDto();
        Long taskId = mdmcChangeMaintainerDto.getTaskId();
        Long maintainerId = mdmcChangeMaintainerDto.getMaintainerId();
        Example example = new Example(MdmcTask.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",taskId);
        if(taskMapper.selectCountByExample(example) == 0){//如果当前任务不存在
            throw new BusinessException(ErrorCodeEnum.MDMC9998098);
        }
        MdmcTask mdmcTask = taskMapper.selectByExample(example).get(0);
        mdmcTask.setMaintainerId(maintainerId);
        mdmcTask.setUpdateInfo(loginAuthDto);
        taskMapper.updateByPrimaryKey(mdmcTask);
        return mdmcTask;
    }


    @Override
    public MdmcChangeStatusDto refuseTaskByMaintainer(RefuseMdmcTaskDto refuseMdmcTaskDto){
        Long taskId = refuseMdmcTaskDto.getTaskId();
        LoginAuthDto loginAuthDto = refuseMdmcTaskDto.getLoginAuthDto();
        MdmcChangeStatusDto mdmcChangeStatusDto = new MdmcChangeStatusDto();
        Example example = new Example(MdmcTask.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",taskId);
        if(taskMapper.selectCountByExample(example) == 0){//如果当前任务不存在
            throw new BusinessException(ErrorCodeEnum.MDMC9998098);
        }
        MdmcTask mdmcTask = taskMapper.selectByExample(example).get(0);
        if(mdmcTask.getStatus()==MdmcTaskStatusEnum.JieDan3.getStatusNum()){
            //如果当前任务的状态处于“已分配维修工，待维修工接单”，工程师才能拒单
            mdmcChangeStatusDto.setLoginAuthDto(loginAuthDto);
            mdmcChangeStatusDto.setStatus(MdmcTaskStatusEnum.Reject2.getStatusNum());
            mdmcChangeStatusDto.setStatusMsg(MdmcTaskStatusEnum.Reject2.getStatusMsg());
            this.modifyTaskStatus(mdmcChangeStatusDto,loginAuthDto);

        }else{
            throw new BusinessException(ErrorCodeEnum.MDMC9998087);
        }
        return mdmcChangeStatusDto;
    }


    @Override
    public MdmcChangeStatusDto refuseTaskByFacilitator(RefuseMdmcTaskDto refuseMdmcTaskDto){
        Long taskId = refuseMdmcTaskDto.getTaskId();
        LoginAuthDto loginAuthDto = refuseMdmcTaskDto.getLoginAuthDto();
        MdmcChangeStatusDto mdmcChangeStatusDto = new MdmcChangeStatusDto();
        Example example = new Example(MdmcTask.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",taskId);
        if(taskMapper.selectCountByExample(example) == 0){//如果当前任务不存在
            throw new BusinessException(ErrorCodeEnum.MDMC9998098);
        }
        MdmcTask mdmcTask = taskMapper.selectByExample(example).get(0);
        if(mdmcTask.getStatus()==MdmcTaskStatusEnum.JieDan1.getStatusNum()){
            //如果当前任务的状态处于“审核通过，待服务商接单”，服务商才能拒单
            mdmcChangeStatusDto.setLoginAuthDto(loginAuthDto);
            mdmcChangeStatusDto.setStatus(MdmcTaskStatusEnum.Reject1.getStatusNum());
            mdmcChangeStatusDto.setStatusMsg(MdmcTaskStatusEnum.Reject1.getStatusMsg());
            this.modifyTaskStatus(mdmcChangeStatusDto,loginAuthDto);

        }else{
            throw new BusinessException(ErrorCodeEnum.MDMC9998087);
        }
        return mdmcChangeStatusDto;
    }


    public String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public void copyPropertiesWithIgnoreNullProperties(Object source, Object target){
        String[] ignore = getNullPropertyNames(source);
        BeanUtils.copyProperties(source, target, ignore);
    }

    @Override
    public List<OptUploadFileRespDto> uploadTaskFile(MultipartHttpServletRequest multipartRequest,OptUploadFileReqDto optUploadFileReqDto, LoginAuthDto loginAuthDto) {
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
                optUploadFileReqDto.setFilePath("ananops/mdmc/" + userId + "/" + filePath+ "/");
                optUploadFileReqDto.setUserId(userId);
                optUploadFileReqDto.setUserName(userName);
                OptUploadFileRespDto optUploadFileRespDto = opcOssFeignApi.uploadFile(optUploadFileReqDto).getResult();
                result.add(optUploadFileRespDto);
            }
        } catch (IOException e) {
            logger.error("上传文件失败={}", e.getMessage(), e);
        }
        System.out.print(result);
        return result;
    }

    @Override
    public List<ElementImgUrlDto> getFileByTaskIdAndStatus(MdmcFileReqDto mdmcFileReqDto) {
        Long id=mdmcFileReqDto.getTaskId();
        Integer status=mdmcFileReqDto.getStatus();
        Example example = new Example(MdmcFileTaskStatus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("taskId",mdmcFileReqDto.getTaskId());
        criteria.andEqualTo("status",mdmcFileReqDto.getStatus());
        if(fileTaskStatusMapper.selectCountByExample(example) == 0){//如果当前查看的图片不存在
            throw new BusinessException(ErrorCodeEnum.OPC10040008,id);
        }
        MdmcFileTaskStatus fileTaskStatus=fileTaskStatusMapper.selectByExample(example).get(0);
        OptBatchGetUrlRequest optBatchGetUrlRequest=new OptBatchGetUrlRequest();
        optBatchGetUrlRequest.setRefNo(String.valueOf(fileTaskStatus.getId()));
        optBatchGetUrlRequest.setEncrypt(true);

        return opcOssFeignApi.listFileUrl(optBatchGetUrlRequest).getResult();
    }

    @Override
    public List<MdmcFileUrlDto> getFileByTaskId(Long taskId) {
        Example example=new Example(MdmcFileTaskStatus.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("taskId",taskId);
        if (fileTaskStatusMapper.selectCountByExample(example)==0){
            throw new BusinessException(ErrorCodeEnum.OPC10040008,taskId);
        }
        List<MdmcFileTaskStatus> fileTaskStatusList=fileTaskStatusMapper.selectByExample(example);
        List<MdmcFileUrlDto> fileUrlDtoList=new ArrayList<>();
        HashMap<Integer,List<ElementImgUrlDto>> map=new HashMap<>();
        map.put(-1, null);
        for (MdmcFileTaskStatus fileTaskStatus:fileTaskStatusList){
            MdmcFileUrlDto fileUrlDto=new MdmcFileUrlDto();
            OptBatchGetUrlRequest optBatchGetUrlRequest=new OptBatchGetUrlRequest();
            int status=fileTaskStatus.getStatus();
            fileUrlDto.setStatus(status);
            optBatchGetUrlRequest.setRefNo(String.valueOf(fileTaskStatus.getId()));
            optBatchGetUrlRequest.setEncrypt(true);
            List<ElementImgUrlDto> imgUrlDtos=opcOssFeignApi.listFileUrl(optBatchGetUrlRequest).getResult();
            if (map.containsKey(status)){
                List<ElementImgUrlDto> t=map.get(status);
                t.addAll(imgUrlDtos);
                map.put(status,t);
                for (MdmcFileUrlDto fileUrlDto1:fileUrlDtoList){
                    if (status==fileUrlDto1.getStatus()){
                        List<ElementImgUrlDto> imgUrlDtos1=fileUrlDto1.getElementImgUrlDtoList();
                        imgUrlDtos1.addAll(imgUrlDtos);
                        break;
                    }
                }
                continue;
            }
            map.put(status,imgUrlDtos);
            fileUrlDto.setElementImgUrlDtoList(imgUrlDtos);
            fileUrlDtoList.add(fileUrlDto);

        }
        return fileUrlDtoList;
    }
}
