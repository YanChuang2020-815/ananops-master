package com.ananops.provider.service.impl;


import com.ananops.PublicUtil;
import com.ananops.base.dto.LoginAuthDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.base.exception.BusinessException;
import com.ananops.core.support.BaseService;
import com.ananops.provider.manager.ImcTaskManager;
import com.ananops.provider.mapper.*;
import com.ananops.provider.model.domain.*;
import com.ananops.provider.model.dto.*;
import com.ananops.provider.model.dto.attachment.OptAttachmentUpdateReqDto;
import com.ananops.provider.model.dto.attachment.OptUploadFileByteInfoReqDto;
import com.ananops.provider.model.dto.group.CompanyDto;
import com.ananops.provider.model.dto.group.GroupSaveDto;
import com.ananops.provider.model.dto.oss.*;
import com.ananops.provider.model.dto.user.UserInfoDto;
import com.ananops.provider.model.enums.ItemStatusEnum;
import com.ananops.provider.model.enums.TaskStatusEnum;
import com.ananops.provider.model.exceptions.UacBizException;
import com.ananops.provider.model.service.UacGroupFeignApi;
import com.ananops.provider.model.service.UacUserFeignApi;
import com.ananops.provider.mq.producer.TaskMsgProducer;
import com.ananops.provider.service.*;
import com.ananops.provider.utils.PdfUtil;
import com.ananops.provider.utils.WaterMark;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.rmi.runtime.Log;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Created by rongshuai on 2019/11/27 19:31
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ImcInspectionTaskServiceImpl extends BaseService<ImcInspectionTask> implements ImcInspectionTaskService {
    @Resource
    ImcInspectionTaskMapper imcInspectionTaskMapper;

    @Resource
    ImcTaskManager imcTaskManager;

    @Resource
    ImcInspectionItemService imcInspectionItemService;

    @Resource
    ImcUserTaskMapper imcUserTaskMapper;

    @Resource
    ImcInspectionItemMapper imcInspectionItemMapper;

    @Resource
    ImcFacilitatorManagerTaskMapper imcFacilitatorManagerTaskMapper;

    @Resource
    ImcFacilitatorGroupTaskMapper imcFacilitatorGroupTaskMapper;

    @Resource
    TaskMsgProducer taskMsgProducer;

    @Resource
    private UacGroupFeignApi uacGroupFeignApi;

    @Resource
    private UacUserFeignApi uacUserFeignApi;

    @Resource
    private PmcProjectFeignApi pmcProjectFeignApi;

    @Resource
    private OpcOssFeignApi opcOssFeignApi;

    @Resource
    private ImcTaskReportMapper imcTaskReportMapper;

    @Resource
    private ImcItemInvoiceService imcItemInvoiceService;

    /**
     * 插入一条巡检任务记录
     * @param imcAddInspectionTaskDto
     * @return
     */
    @Override
    public ImcAddInspectionTaskDto saveTask(ImcAddInspectionTaskDto imcAddInspectionTaskDto, LoginAuthDto loginAuthDto){
        ImcInspectionTask imcInspectionTask = new ImcInspectionTask();
        BeanUtils.copyProperties(imcAddInspectionTaskDto,imcInspectionTask);
        imcInspectionTask.setUpdateInfo(loginAuthDto);
        if(imcInspectionTask.isNew()){
            //如果当前是新建一条任务
            //获取所有的巡检任务子项
            List<ImcAddInspectionItemDto> imcAddInspectionItemDtoList = imcAddInspectionTaskDto.getImcAddInspectionItemDtoList();
            Integer days = imcAddInspectionTaskDto.getDays();
            Date startTime = imcAddInspectionTaskDto.getScheduledStartTime();
            Calendar calendar = new GregorianCalendar();
            Integer inspectionType = imcAddInspectionTaskDto.getInspectionType();
            // 新建巡检任务
            Long taskId = super.generateId();
            Long userId = imcAddInspectionTaskDto.getUserId();
            Long facilitatorManagerId = imcAddInspectionTaskDto.getFacilitatorManagerId();
            Long facilitatorGroupId = imcAddInspectionTaskDto.getFacilitatorGroupId();
            imcInspectionTask.setId(taskId);
            imcInspectionTask.setScheduledStartTime(startTime);
            if(inspectionType==1){
                //如果是从巡检方案中发起的，则无需甲方负责人审核
                imcInspectionTask.setStatus(TaskStatusEnum.WAITING_FOR_ACCEPT.getStatusNum());
            }else{
                //将巡检任务的状态设置为等待甲方负责人审核
                imcInspectionTask.setStatus(TaskStatusEnum.WAITING_FOR_PRINCIPAL.getStatusNum());
            }
            imcInspectionTaskMapper.insert(imcInspectionTask);
            logger.info("新创建一条巡检记录：" + imcInspectionTask.toString());
            //更新startTime
            calendar.setTime(startTime);
            calendar.add(calendar.DATE,days);//当前时间向后增加days天
            startTime=calendar.getTime();

            //增加一条甲方用户和巡检任务的关系记录
            ImcUserTask imcUserTask = new ImcUserTask();
            imcUserTask.setTaskId(taskId);
            imcUserTask.setUserId(userId);
            imcUserTaskMapper.insert(imcUserTask);

            //增加一条服务商管理员和巡检任务的关系记录
            ImcFacilitatorManagerTask imcFacilitatorManagerTask = new ImcFacilitatorManagerTask();
            imcFacilitatorManagerTask.setFacilitatorManagerId(facilitatorManagerId);
            imcFacilitatorManagerTask.setTaskId(taskId);
            imcFacilitatorManagerTaskMapper.insert(imcFacilitatorManagerTask);

            //增加一条服务商组织和巡检任务的关系记录
            ImcFacilitatorGroupTask imcFacilitatorGroupTask = new ImcFacilitatorGroupTask();
            imcFacilitatorGroupTask.setFacilitatorGroupId(facilitatorGroupId);
            imcFacilitatorGroupTask.setTaskId(taskId);
            imcFacilitatorGroupTaskMapper.insert(imcFacilitatorGroupTask);
            if(imcAddInspectionItemDtoList!=null){
                //保存新创建的巡检任务子项
                imcAddInspectionItemDtoList.forEach(item->{
                    //保存所有巡检任务子项
                    item.setInspectionTaskId(taskId);//设置巡检任务子项对应的任务id
                    item.setDays(imcInspectionTask.getDays());//设置巡检任务子项对应的巡检周期
                    item.setFrequency(imcInspectionTask.getFrequency());//设置巡检任务子项对应的巡检频率
                    item.setScheduledStartTime(imcInspectionTask.getScheduledStartTime());//设置巡检任务子项的对应的计划开始时间
                    item.setUserId(userId);
                    item.setStatus(ItemStatusEnum.WAITING_FOR_MAINTAINER.getStatusNum());
                    Long scheduledStartTime = item.getScheduledStartTime().getTime();//获得巡检任务子项的预计开始时间
                    Long currentTime = System.currentTimeMillis();//获得当前时间
                    if(scheduledStartTime<=currentTime){//如果计划执行时间<=当前时间，说明，巡检任务需要立即执行
                        //将巡检任务子项的状态设置为等待分配工程师
                        item.setStatus(ItemStatusEnum.WAITING_FOR_MAINTAINER.getStatusNum());
                    }
                    //创建新的任务子项，并更新返回结果
                    BeanUtils.copyProperties(imcInspectionItemService.saveInspectionItem(item,loginAuthDto),item);
                });
                BeanUtils.copyProperties(imcAddInspectionItemDtoList,imcAddInspectionTaskDto);
            }
            //更新返回结果
            BeanUtils.copyProperties(imcInspectionTask,imcAddInspectionTaskDto);
            //推送消息
            MqMessageData mqMessageData = taskMsgProducer.sendTaskStatusMsgMq(imcInspectionTask);
            imcTaskManager.modifyTaskStatus(mqMessageData);
        }else{
            //如果当前是更新一条记录
            imcInspectionTaskMapper.updateByPrimaryKeySelective(imcInspectionTask);
            //更新返回结果
            BeanUtils.copyProperties(imcInspectionTask,imcAddInspectionTaskDto);
        }
        System.out.println(imcInspectionTask.getTaskName());
        return imcAddInspectionTaskDto;
    }

    /**
     * 根据巡检任务的Id获取巡检任务
     * @param taskId
     * @return
     */
    @Override
    public ImcInspectionTask getTaskByTaskId(Long taskId){//根据巡检任务的ID，获取巡检任务的详情
        return imcInspectionTaskMapper.selectByPrimaryKey(taskId);
    }

    @Override
    public ImcInspectionTaskDto getTaskDtoByTaskId(Long taskId) {
        ImcInspectionTask imcInspectionTask = imcInspectionTaskMapper.selectByPrimaryKey(taskId);
        if (imcInspectionTask == null) {
            return new ImcInspectionTaskDto();
        }
        List<ImcInspectionTask> imcInspectionTasks = new ArrayList<>();
        imcInspectionTasks.add(imcInspectionTask);
        return (ImcInspectionTaskDto)transform(imcInspectionTasks).get(0);
    }

    /**
     * 修改巡检任务的状态
     * @param imcTaskChangeStatusDto
     * @param loginAuthDto
     * @return
     */
    @Override
    public ImcInspectionTask modifyTaskStatus(ImcTaskChangeStatusDto imcTaskChangeStatusDto, LoginAuthDto loginAuthDto){
        Long taskId = imcTaskChangeStatusDto.getTaskId();
        Integer status = imcTaskChangeStatusDto.getStatus();
        ImcInspectionTask imcInspectionTask = new ImcInspectionTask();
        Example example = new Example(ImcInspectionTask.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",taskId);
        if(imcInspectionTaskMapper.selectCountByExample(example)==0){
            //如果当前任务不存在
            throw new BusinessException(ErrorCodeEnum.GL9999098,taskId);
        }
        //如果当前任务存在
        imcInspectionTask = imcInspectionTaskMapper.selectByPrimaryKey(taskId);
        if(status.equals(TaskStatusEnum.WAITING_FOR_PAY.getStatusNum())){
            //如果当前任务状态修改为等待支付，意味着任务已经被确认
            Example example2 = new Example(ImcInspectionItem.class);
            Example.Criteria criteria2 = example2.createCriteria();
            criteria2.andEqualTo("inspectionTaskId",taskId);
            List<ImcInspectionItem> imcInspectionItemList = imcInspectionItemMapper.selectByExample(example2);
            imcInspectionItemList.forEach(imcInspectionItem -> {
                //任务已经巡检完毕，将全部任务子项的状态修改为已完成
                imcInspectionItem.setStatus(ItemStatusEnum.VERIFIED.getStatusNum());
                imcInspectionItemService.update(imcInspectionItem);
                //用户确认完成后需要将巡检单据中的用户确认字段填入
                imcItemInvoiceService.handleUserConfirm(imcInspectionItem.getId(), loginAuthDto);
            });
        }
        else if(status.equals(TaskStatusEnum.WAITING_FOR_CONFIRM.getStatusNum())){
            //如果当前状态处于巡检完成等待甲方负责人确认的阶段
            //更新巡检完成的实际时间
            imcInspectionTask.setActualFinishTime(new Date(System.currentTimeMillis()));
        }else if(TaskStatusEnum.INSPECTION_OVER.getStatusNum()==status){
            //如果巡检结束，自动生成附件
            this.generateImcTaskPdf(taskId,loginAuthDto);
        }
        imcInspectionTask.setStatus(status);
        imcInspectionTask.setUpdateInfo(loginAuthDto);
        int result = imcInspectionTaskMapper.updateByPrimaryKeySelective(imcInspectionTask);
        if(result < 1){
            throw new BusinessException(ErrorCodeEnum.GL9999092);
        }
        MqMessageData mqMessageData = taskMsgProducer.sendTaskStatusMsgMq(imcInspectionTask);
        imcTaskManager.modifyTaskStatus(mqMessageData);
        return imcInspectionTask;
    }

    /**
     * 删除指定的巡检任务以及对应的子项
     * @param taskId
     */
    @Override
    public void deleteTaskById(Long taskId){
        Example example = new Example(ImcInspectionTask.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",taskId);
        ItemQueryDto itemQueryDto = new ItemQueryDto();
        itemQueryDto.setTaskId(taskId);
        List<ImcInspectionItem> imcInspectionItems = imcInspectionItemService.getAllItemByTaskId(itemQueryDto);
        for(int i=0;i<imcInspectionItems.size();i++){
            Long itemId = imcInspectionItems.get(i).getId();
            imcInspectionItemService.deleteItemByItemId(itemId);
        }
        imcInspectionTaskMapper.deleteByExample(example);
    }

    /**
     * 获得某一状态下的全部巡检任务（可分页）
     * @param taskQueryDto
     * @return
     */
    @Override
    public PageInfo getTaskByStatusAndPage(TaskQueryDto taskQueryDto){
        Example example = new Example(ImcInspectionTask.class);
        Example.Criteria criteria = example.createCriteria();
        Integer status = taskQueryDto.getStatus();
        String taskName = taskQueryDto.getTaskName();
        if(StringUtils.isNotBlank(taskName)){
            taskName = "%" + taskName + "%";
            criteria.andLike("taskName",taskName);
        }
        criteria.andEqualTo("status",status);
        example.setOrderByClause("update_time DESC");
        if(imcInspectionTaskMapper.selectCountByExample(example)==0){
            //当前状态没有对应的任务
            throw new BusinessException(ErrorCodeEnum.GL9999091);
        }
        PageHelper.startPage(taskQueryDto.getPageNum(),taskQueryDto.getPageSize());
        List<ImcInspectionTask> imcInspectionTaskList = imcInspectionTaskMapper.selectByExample(example);
        return new PageInfo<>(imcInspectionTaskList);
    }

    /**
     * 获得某一状态下的全部巡检任务
     * @param taskQueryDto
     * @return
     */
    @Override
    public List<ImcInspectionTask> getTaskByStatus(TaskQueryDto taskQueryDto){
        Example example = new Example(ImcInspectionTask.class);
        Example.Criteria criteria = example.createCriteria();
        Integer status = taskQueryDto.getStatus();
        String taskName = taskQueryDto.getTaskName();
        if(StringUtils.isNotBlank(taskName)){
            taskName = "%" + taskName + "%";
            criteria.andLike("taskName",taskName);
        }
        criteria.andEqualTo("status",status);
        if(imcInspectionTaskMapper.selectCountByExample(example)==0){
            //当前状态没有对应的任务
            throw new BusinessException(ErrorCodeEnum.GL9999091);
        }
        PageHelper.startPage(taskQueryDto.getPageNum(),taskQueryDto.getPageSize());
        return imcInspectionTaskMapper.selectByExample(example);
    }

    /**
     * 修改巡检任务的名字
     * @param taskNameChangeDto
     * @param loginAuthDto
     * @return
     */
    @Override
    public ImcInspectionTask modifyTaskName(TaskNameChangeDto taskNameChangeDto, LoginAuthDto loginAuthDto){
        ImcInspectionTask imcInspectionTask = new ImcInspectionTask();
        Example example = new Example(ImcInspectionTask.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",taskNameChangeDto.getTaskId());
        if(imcInspectionTaskMapper.selectCountByExample(example)==0){
            throw new BusinessException(ErrorCodeEnum.GL9999098);
        }
        imcInspectionTask = imcInspectionTaskMapper.selectByExample(example).get(0);
        imcInspectionTask.setTaskName(taskNameChangeDto.getTaskName());
        imcInspectionTask.setUpdateInfo(loginAuthDto);
        imcInspectionTaskMapper.updateByPrimaryKeySelective(imcInspectionTask);
        return imcInspectionTask;
    }

    /**
     * 根据项目id查询对应的巡检任务（可分页）
     * @param taskQueryDto
     * @return
     */
    @Override
    public PageInfo getTaskByProjectIdAndPage(TaskQueryDto taskQueryDto){
        Example example = new Example(ImcInspectionTask.class);
        Example.Criteria criteria = example.createCriteria();
        Long projectId = taskQueryDto.getProjectId();
        String taskName = taskQueryDto.getTaskName();
        if(StringUtils.isNotBlank(taskName)){
            taskName = "%" + taskName + "%";
            criteria.andLike("taskName",taskName);
        }
        criteria.andEqualTo("projectId",projectId);

        if(imcInspectionTaskMapper.selectCountByExample(example)==0){
            //当前状态没有对应的任务
            throw new BusinessException(ErrorCodeEnum.GL9999091);
        }
        example.setOrderByClause("update_time DESC");
        PageHelper.startPage(taskQueryDto.getPageNum(),taskQueryDto.getPageSize());
        List<ImcInspectionTask> imcInspectionTaskList = imcInspectionTaskMapper.selectByExample(example);
        return new PageInfo<>(imcInspectionTaskList);
    }
    /**
     * 根据项目id查询对应的巡检任务
     * @param taskQueryDto
     * @return
     */
    @Override
    public List<ImcInspectionTask> getTaskByProjectId(TaskQueryDto taskQueryDto){
        Example example = new Example(ImcInspectionTask.class);
        Example.Criteria criteria = example.createCriteria();
        Long projectId = taskQueryDto.getProjectId();
        String taskName = taskQueryDto.getTaskName();
        if(StringUtils.isNotBlank(taskName)){
            taskName = "%" + taskName + "%";
            criteria.andLike("taskName",taskName);
        }
        criteria.andEqualTo("projectId",projectId);
        if(imcInspectionTaskMapper.selectCountByExample(example)==0){
            //当前状态没有对应的任务
            throw new BusinessException(ErrorCodeEnum.GL9999091);
        }
        PageHelper.startPage(taskQueryDto.getPageNum(),taskQueryDto.getPageSize());
        return imcInspectionTaskMapper.selectByExample(example);
    }

    /**
     * 根据用户id和用户角色获取全部的巡检任务数目
     * @param taskQueryDto
     * @return
     */
    @Override
    public Integer getImcTaskNumberByUserIdAndRole(TaskQueryDto taskQueryDto){
        Integer role = taskQueryDto.getRole();
        Long userId = taskQueryDto.getUserId();
        if(role==null||userId==null){
            throw new BusinessException(ErrorCodeEnum.IMC10090008);
        }else{
            switch (role){
                case 1://如果角色是甲方负责人
                    return imcInspectionTaskMapper.countTaskByUserId(userId);
                case 2://如果角色是服务商
                    return this.countTaskByFacilitatorId(userId);
                default:
                    throw new BusinessException(ErrorCodeEnum.GL9999089);
            }
        }
    }


    /**
     * 根据用户id获取对应的巡检任务（可分页）
     * @param taskQueryDto
     * @return
     */
    @Override
    public PageInfo getTaskByUserIdAndPage(TaskQueryDto taskQueryDto){
        Integer role = taskQueryDto.getRole();
        Page page = PageHelper.startPage(taskQueryDto.getPageNum(),taskQueryDto.getPageSize());
        PageInfo pageInfo;
        String taskName = taskQueryDto.getTaskName();
        List<ImcInspectionTask> imcInspectionTaskList;
        if(StringUtils.isNotBlank(taskName)){
            taskName = "%" + taskName + "%";
            if(role == null){
                throw new BusinessException(ErrorCodeEnum.GL9999089);
            }
            switch (role){
                case 1://如果角色是甲方用户
                    imcInspectionTaskList = imcInspectionTaskMapper.queryTaskByUserIdAndTaskName(taskQueryDto.getUserId(),taskName);
                    pageInfo = new PageInfo<>(transform(imcInspectionTaskList));
                    pageInfo.setTotal(page.getTotal());
                    pageInfo.setPages(page.getPages());
                    return pageInfo;
                case 2://如果角色是服务商
                    imcInspectionTaskList = this.getTaskByFacilitatorId(taskQueryDto);
                    pageInfo = new PageInfo<>(transform(imcInspectionTaskList));
                    pageInfo.setTotal(page.getTotal());
                    pageInfo.setPages(page.getPages());
                    return pageInfo;
                case 3://如果角色是服务商管理员
                    imcInspectionTaskList = imcInspectionTaskMapper.queryTaskByFacilitatorManagerIdAndTaskName(taskQueryDto.getUserId(),taskName);
                    pageInfo = new PageInfo<>(transform(imcInspectionTaskList));
                    pageInfo.setTotal(page.getTotal());
                    pageInfo.setPages(page.getPages());
                    return pageInfo;
                case 4://如果角色是服务商组织
                    imcInspectionTaskList = imcInspectionTaskMapper.queryTaskByFacilitatorGroupIdAndTaskName(getCompanyGroupIdFromUserGroupId(taskQueryDto.getUserId()),taskName);
                    pageInfo = new PageInfo<>(transform(imcInspectionTaskList));
                    pageInfo.setTotal(page.getTotal());
                    pageInfo.setPages(page.getPages());
                    return pageInfo;
                default:
                    throw new BusinessException(ErrorCodeEnum.GL9999089);
            }
        }else{
            if(role == null){
                throw new BusinessException(ErrorCodeEnum.GL9999089);
            }
            switch (role){
                case 1://如果角色是甲方用户
                    imcInspectionTaskList = imcInspectionTaskMapper.queryTaskByUserId(taskQueryDto.getUserId());
                    pageInfo = new PageInfo<>(transform(imcInspectionTaskList));
                    pageInfo.setTotal(page.getTotal());
                    pageInfo.setPages(page.getPages());
                    return pageInfo;
                case 2://如果角色是服务商
                    imcInspectionTaskList = this.getTaskByFacilitatorId(taskQueryDto);
                    pageInfo = new PageInfo<>(transform(imcInspectionTaskList));
                    pageInfo.setTotal(page.getTotal());
                    pageInfo.setPages(page.getPages());
                    return pageInfo;
                case 3://如果角色是服务商管理员
                    imcInspectionTaskList = imcInspectionTaskMapper.queryTaskByFacilitatorManagerId(taskQueryDto.getUserId());
                    pageInfo = new PageInfo<>(transform(imcInspectionTaskList));
                    pageInfo.setTotal(page.getTotal());
                    pageInfo.setPages(page.getPages());
                    return pageInfo;
                case 4://如果角色是服务商组织
                    imcInspectionTaskList = imcInspectionTaskMapper.queryTaskByFacilitatorGroupId(getCompanyGroupIdFromUserGroupId(taskQueryDto.getUserId()));
                    pageInfo = new PageInfo<>(transform(imcInspectionTaskList));
                    pageInfo.setTotal(page.getTotal());
                    pageInfo.setPages(page.getPages());
                    return pageInfo;
                default:
                    throw new BusinessException(ErrorCodeEnum.GL9999089);
            }
        }
    }
    /**
     * 根据用户id获取对应的巡检任务
     * @param taskQueryDto
     * @return
     */
    @Override
    public List<ImcInspectionTask> getTaskByUserId(TaskQueryDto taskQueryDto){
        Integer role = taskQueryDto.getRole();
        PageHelper.startPage(taskQueryDto.getPageNum(),taskQueryDto.getPageSize());
        String taskName = taskQueryDto.getTaskName();
        if(StringUtils.isNotBlank(taskName)){
            taskName = "%" + taskName + "%";
            if(role == null){
                throw new BusinessException(ErrorCodeEnum.GL9999089);
            }
            switch (role){
                case 1://如果角色是甲方用户
                    return imcInspectionTaskMapper.queryTaskByUserIdAndTaskName(taskQueryDto.getUserId(),taskName);
                case 2://如果角色是服务商
                    return this.getTaskByFacilitatorId(taskQueryDto);
                case 3://如果角色是服务商管理员
                    return imcInspectionTaskMapper.queryTaskByFacilitatorManagerIdAndTaskName(taskQueryDto.getUserId(),taskName);
                case 4://如果角色是服务商组织
                    return imcInspectionTaskMapper.queryTaskByFacilitatorGroupIdAndTaskName(getCompanyGroupIdFromUserGroupId(taskQueryDto.getUserId()),taskName);
                default:
                    throw new BusinessException(ErrorCodeEnum.GL9999089);
            }
        }else{
            if(role == null){
                throw new BusinessException(ErrorCodeEnum.GL9999089);
            }
            switch (role){
                case 1://如果角色是甲方用户
                    return imcInspectionTaskMapper.queryTaskByUserId(taskQueryDto.getUserId());
                case 2://如果角色是服务商
                    return this.getTaskByFacilitatorId(taskQueryDto);
                case 3://如果角色是服务商管理员
                    return imcInspectionTaskMapper.queryTaskByFacilitatorManagerId(taskQueryDto.getUserId());
                case 4://如果角色是服务商组织
                    return imcInspectionTaskMapper.queryTaskByFacilitatorGroupId(getCompanyGroupIdFromUserGroupId(taskQueryDto.getUserId()));
                default:
                    throw new BusinessException(ErrorCodeEnum.GL9999089);
            }
        }
    }

    /**
     * 根据用户Id查询对应状态的巡检任务（可分页）
     * @param taskQueryDto
     * @return
     */
    @Override
    public PageInfo getTaskByUserIdAndStatusAndPage(TaskQueryDto taskQueryDto){
        Integer role = taskQueryDto.getRole();
        Page page = PageHelper.startPage(taskQueryDto.getPageNum(),taskQueryDto.getPageSize());
        PageInfo pageInfo;
        String taskName = taskQueryDto.getTaskName();
        List<ImcInspectionTask> imcInspectionTaskList;
        if(StringUtils.isNotBlank(taskName)){
            taskName = "%" + taskName + "%";
            switch (role){
                case 1://如果角色是甲方用户
                    imcInspectionTaskList = imcInspectionTaskMapper.queryTaskByUserIdAndStatusAndTaskName(taskQueryDto.getUserId(),taskQueryDto.getStatus(),taskName);
                    pageInfo = new PageInfo<>(transform(imcInspectionTaskList));
                    pageInfo.setTotal(page.getTotal());
                    pageInfo.setPages(page.getPages());
                    return pageInfo;
                case 2://如果角色是服务商
                    imcInspectionTaskList = this.getTaskByFacilitatorIdAndStatus(taskQueryDto);
                    pageInfo = new PageInfo<>(transform(imcInspectionTaskList));
                    pageInfo.setTotal(page.getTotal());
                    pageInfo.setPages(page.getPages());
                    return pageInfo;
                case 3://如果角色是服务商管理员
                    imcInspectionTaskList = imcInspectionTaskMapper.queryTaskByFacilitatorManagerIdAndStatusAndTaskName(taskQueryDto.getUserId(),taskQueryDto.getStatus(),taskName);
                    pageInfo = new PageInfo<>(transform(imcInspectionTaskList));
                    pageInfo.setTotal(page.getTotal());
                    pageInfo.setPages(page.getPages());
                    return pageInfo;
                case 4://如果角色是服务商组织
                    imcInspectionTaskList = imcInspectionTaskMapper.queryTaskByFacilitatorGroupIdAndStatusAndTaskName(getCompanyGroupIdFromUserGroupId(taskQueryDto.getUserId()),taskQueryDto.getStatus(),taskName);
                    pageInfo = new PageInfo<>(transform(imcInspectionTaskList));
                    pageInfo.setTotal(page.getTotal());
                    pageInfo.setPages(page.getPages());
                    return pageInfo;
                default:
                    throw new BusinessException(ErrorCodeEnum.GL9999089);
            }
        }else{
            switch (role){
                case 1://如果角色是甲方用户
                    imcInspectionTaskList = imcInspectionTaskMapper.queryTaskByUserIdAndStatus(taskQueryDto.getUserId(),taskQueryDto.getStatus());
                    pageInfo = new PageInfo<>(transform(imcInspectionTaskList));
                    pageInfo.setTotal(page.getTotal());
                    pageInfo.setPages(page.getPages());
                    return pageInfo;
                case 2://如果角色是服务商
                    imcInspectionTaskList = this.getTaskByFacilitatorIdAndStatus(taskQueryDto);
                    pageInfo = new PageInfo<>(transform(imcInspectionTaskList));
                    pageInfo.setTotal(page.getTotal());
                    pageInfo.setPages(page.getPages());
                    return pageInfo;
                case 3://如果角色是服务商管理员
                    imcInspectionTaskList = imcInspectionTaskMapper.queryTaskByFacilitatorManagerIdAndStatus(taskQueryDto.getUserId(),taskQueryDto.getStatus());
                    pageInfo = new PageInfo<>(transform(imcInspectionTaskList));
                    pageInfo.setTotal(page.getTotal());
                    pageInfo.setPages(page.getPages());
                    return pageInfo;
                case 4://如果角色是服务商组织
                    imcInspectionTaskList = imcInspectionTaskMapper.queryTaskByFacilitatorGroupIdAndStatus(getCompanyGroupIdFromUserGroupId(taskQueryDto.getUserId()),taskQueryDto.getStatus());
                    pageInfo = new PageInfo<>(transform(imcInspectionTaskList));
                    pageInfo.setTotal(page.getTotal());
                    pageInfo.setPages(page.getPages());
                    return pageInfo;
                default:
                    throw new BusinessException(ErrorCodeEnum.GL9999089);
            }
        }
    }
    /**
     * 根据用户Id查询对应状态的巡检任务
     * @param taskQueryDto
     * @return
     */
    @Override
    public List<ImcInspectionTask> getTaskByUserIdAndStatus(TaskQueryDto taskQueryDto){
        Integer role = taskQueryDto.getRole();
        PageHelper.startPage(taskQueryDto.getPageNum(),taskQueryDto.getPageSize());
        String taskName = taskQueryDto.getTaskName();
        if(StringUtils.isNotBlank(taskName)){
            taskName = "%" + taskName + "%";
            switch (role){
                case 1://如果角色是甲方用户
                    return imcInspectionTaskMapper.queryTaskByUserIdAndStatusAndTaskName(taskQueryDto.getUserId(),taskQueryDto.getStatus(),taskName);
                case 2://如果角色是服务商
                    return this.getTaskByFacilitatorIdAndStatus(taskQueryDto);
                case 3://如果角色是服务商管理员
                    return imcInspectionTaskMapper.queryTaskByFacilitatorManagerIdAndStatusAndTaskName(taskQueryDto.getUserId(),taskQueryDto.getStatus(),taskName);
                case 4://如果角色是服务商组织
                    return imcInspectionTaskMapper.queryTaskByFacilitatorGroupIdAndStatusAndTaskName(getCompanyGroupIdFromUserGroupId(taskQueryDto.getUserId()),taskQueryDto.getStatus(),taskName);
                default:
                    throw new BusinessException(ErrorCodeEnum.GL9999089);
            }
        }else{
            switch (role){
                case 1://如果角色是甲方用户
                    return imcInspectionTaskMapper.queryTaskByUserIdAndStatus(taskQueryDto.getUserId(),taskQueryDto.getStatus());
                case 2://如果角色是服务商
                    return this.getTaskByFacilitatorIdAndStatus(taskQueryDto);
                case 3://如果角色是服务商管理员
                    return imcInspectionTaskMapper.queryTaskByFacilitatorManagerIdAndStatus(taskQueryDto.getUserId(),taskQueryDto.getStatus());
                case 4://如果角色是服务商组织
                    return imcInspectionTaskMapper.queryTaskByFacilitatorGroupIdAndStatus(getCompanyGroupIdFromUserGroupId(taskQueryDto.getUserId()),taskQueryDto.getStatus());
                default:
                    throw new BusinessException(ErrorCodeEnum.GL9999089);
            }
        }
    }

    /**
     * 根据服务商Id查询服务商对应的巡检任务（可分页）
     * @param taskQueryDto
     * @return
     */
    @Override
    public PageInfo getTaskByFacilitatorIdAndPage(TaskQueryDto taskQueryDto,LoginAuthDto loginAuthDto){
        // 获取登录用户的组织Id
        Long userGroupId = loginAuthDto.getGroupId();
        // 根据组织Id查询公司Id
        Long groupId = uacGroupFeignApi.getCompanyInfoById(userGroupId).getResult().getId();
        Example example = new Example(ImcInspectionTask.class);
        Example.Criteria criteria = example.createCriteria();
        String taskName = taskQueryDto.getTaskName();
        if(StringUtils.isNotBlank(taskName)){
            taskName = "%" + taskName + "%";
            criteria.andLike("taskName",taskName);
        }
        criteria.andEqualTo("facilitatorId",groupId);
        example.setOrderByClause("update_time DESC");
        Page page = PageHelper.startPage(taskQueryDto.getPageNum(),taskQueryDto.getPageSize());
        List<ImcInspectionTask> imcInspectionTaskList = imcInspectionTaskMapper.selectByExample(example);
        PageInfo pageInfo = new PageInfo<>(transform(imcInspectionTaskList));
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPages(page.getPages());
        return pageInfo;
    }

    @Override
    public PageInfo getAllFinishedTaskByFacilitatorIdAndPage(LoginAuthDto loginAuthDto,TaskQueryDto taskQueryDto){
        // 获取登录用户的组织Id
        Long userGroupId = loginAuthDto.getGroupId();
        // 根据组织Id查询公司Id
        Long groupId = uacGroupFeignApi.getCompanyInfoById(userGroupId).getResult().getId();
        Page page = PageHelper.startPage(taskQueryDto.getPageNum(),taskQueryDto.getPageSize());
        List<ImcInspectionTask> imcInspectionTaskList = imcInspectionTaskMapper.queryAllFinishedTaskByFacilitatorId(groupId);
        PageInfo pageInfo = new PageInfo<>(transform(imcInspectionTaskList));
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPages(page.getPages());
        return pageInfo;
    }

    /**
     * 根据服务商Id查询服务商对应的巡检任务
     * @param taskQueryDto
     * @return
     */
    @Override
    public List<ImcInspectionTask> getTaskByFacilitatorId(TaskQueryDto taskQueryDto){
//        PageHelper.startPage(taskQueryDto.getPageNum(),taskQueryDto.getPageSize());
        Long facilitatorId = taskQueryDto.getUserId();
        Example example = new Example(ImcInspectionTask.class);
        Example.Criteria criteria = example.createCriteria();
        String taskName = taskQueryDto.getTaskName();
        if(StringUtils.isNotBlank(taskName)){
            taskName = "%" + taskName + "%";
            criteria.andLike("taskName",taskName);
        }
        criteria.andEqualTo("facilitatorId",facilitatorId);
        return imcInspectionTaskMapper.selectByExample(example);
    }

    public Integer countTaskByFacilitatorId(Long facilitatorId){
        Example example = new Example(ImcInspectionTask.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("facilitatorId",facilitatorId);
        return imcInspectionTaskMapper.selectCountByExample(example);
    }

    /**
     * 根据服务商id查询对应状态的巡检任务（可分页）
     * @param taskQueryDto
     * @return
     */
    @Override
    public PageInfo getTaskByFacilitatorIdAndStatusAndPage(TaskQueryDto taskQueryDto){
        PageHelper.startPage(taskQueryDto.getPageNum(),taskQueryDto.getPageSize());
        Long facilitatorId = taskQueryDto.getUserId();
        Integer status = taskQueryDto.getStatus();
        Example example = new Example(ImcInspectionTask.class);
        Example.Criteria criteria = example.createCriteria();
        String taskName = taskQueryDto.getTaskName();
        if(StringUtils.isNotBlank(taskName)){
            taskName = "%" + taskName + "%";
            criteria.andLike("taskName",taskName);
        }
        criteria.andEqualTo("facilitatorId",facilitatorId);
        criteria.andEqualTo("status",status);
        example.setOrderByClause("update_time DESC");
        List<ImcInspectionTask> imcInspectionTaskList = imcInspectionTaskMapper.selectByExample(example);
        return new PageInfo<>(imcInspectionTaskList);
    }

    /**
     * 查询全部当前服务商未接单的巡检任务
     * @param loginAuthDto
     * @return
     */
    @Override
    public PageInfo getAllUnConfirmedTask(LoginAuthDto loginAuthDto,TaskQueryDto taskQueryDto){
        // 获取登录用户的组织Id
        Long userGroupId = loginAuthDto.getGroupId();
        // 根据组织Id查询公司Id
        Long groupId = uacGroupFeignApi.getCompanyInfoById(userGroupId).getResult().getId();
        Example example = new Example(ImcInspectionTask.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("facilitatorId",groupId);
        criteria.andEqualTo("status",TaskStatusEnum.WAITING_FOR_ACCEPT.getStatusNum());
        example.setOrderByClause("update_time DESC");
        Page page = PageHelper.startPage(taskQueryDto.getPageNum(),taskQueryDto.getPageSize());
        List<ImcInspectionTask> imcInspectionTaskList = imcInspectionTaskMapper.selectByExample(example);
        PageInfo pageInfo = new PageInfo<>(transform(imcInspectionTaskList));
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPages(page.getPages());
        return pageInfo;
    }

    /**
     * 查询全部当前服务商未分配工程师的工单
     * @param loginAuthDto
     * @return
     */
    @Override
    public PageInfo getAllUnDistributedTask(LoginAuthDto loginAuthDto,TaskQueryDto taskQueryDto){
        // 获取登录用户的组织Id
        Long userGroupId = loginAuthDto.getGroupId();
        // 根据组织Id查询公司Id
        Long groupId = uacGroupFeignApi.getCompanyInfoById(userGroupId).getResult().getId();
        Page page = PageHelper.startPage(taskQueryDto.getPageNum(),taskQueryDto.getPageSize());
        List<ImcInspectionTask> imcInspectionTaskList = imcInspectionTaskMapper.queryAllUnDistributedTask(groupId);
        PageInfo pageInfo = new PageInfo<>(transform(imcInspectionTaskList));
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPages(page.getPages());
        return pageInfo;
    }

    /**
     * 根据服务商id查询对应状态的巡检任务
     * @param taskQueryDto
     * @return
     */
    @Override
    public List<ImcInspectionTask> getTaskByFacilitatorIdAndStatus(TaskQueryDto taskQueryDto){
        PageHelper.startPage(taskQueryDto.getPageNum(),taskQueryDto.getPageSize());
        // 登录用户的组织Id
        Long userGroupId = taskQueryDto.getUserId();
        // 获取该用户所属公司Id
        Long facilitatorId = getCompanyGroupIdFromUserGroupId(userGroupId); // 根据组织Id查询公司Id
        Integer status = taskQueryDto.getStatus();
        Example example = new Example(ImcInspectionTask.class);
        Example.Criteria criteria = example.createCriteria();
        String taskName = taskQueryDto.getTaskName();
        if(StringUtils.isNotBlank(taskName)){
            taskName = "%" + taskName + "%";
            criteria.andLike("taskName",taskName);
        }
        criteria.andEqualTo("facilitatorId",facilitatorId);
        criteria.andEqualTo("status",status);
        return imcInspectionTaskMapper.selectByExample(example);
    }

    /**
     * 查询当前甲方负责人下全部未授权的巡检任务（可分页）
     * @param taskQueryDto
     * @return
     */
    @Override
    public PageInfo getAllUnauthorizedTaskByPrincipalIdAndPage(TaskQueryDto taskQueryDto){
        PageHelper.startPage(taskQueryDto.getPageNum(),taskQueryDto.getPageSize());
        Long principalId = taskQueryDto.getUserId();
        Example example = new Example(ImcInspectionTask.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("principalId",principalId);
        criteria.andEqualTo("status",TaskStatusEnum.WAITING_FOR_PRINCIPAL.getStatusNum());
        example.setOrderByClause("update_time DESC");
        List<ImcInspectionTask> imcInspectionTaskList = imcInspectionTaskMapper.selectByExample(example);
        return new PageInfo<>(imcInspectionTaskList);
    }
    /**
     * 查询当前甲方负责人下全部未授权的巡检任务
     * @param taskQueryDto
     * @return
     */
    @Override
    public List<ImcInspectionTask> getAllUnauthorizedTaskByPrincipalId(TaskQueryDto taskQueryDto){
        PageHelper.startPage(taskQueryDto.getPageNum(),taskQueryDto.getPageSize());
        Long principalId = taskQueryDto.getUserId();
        Example example = new Example(ImcInspectionTask.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("principalId",principalId);
        criteria.andEqualTo("status",TaskStatusEnum.WAITING_FOR_PRINCIPAL.getStatusNum());
        return imcInspectionTaskMapper.selectByExample(example);
    }

    /**
     * 查询当前甲方负责人下全部被否决的巡检任务（可分页）
     * @param taskQueryDto
     * @return
     */
    @Override
    public PageInfo getAllDeniedTaskByPrincipalIdAndPage(TaskQueryDto taskQueryDto){
        PageHelper.startPage(taskQueryDto.getPageNum(),taskQueryDto.getPageSize());
        Long principalId = taskQueryDto.getUserId();
        Example example = new Example(ImcInspectionTask.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("principalId",principalId);
        criteria.andEqualTo("status",TaskStatusEnum.NO_SUCH_STATUS.getStatusNum());
        example.setOrderByClause("update_time DESC");
        List<ImcInspectionTask> imcInspectionTaskList = imcInspectionTaskMapper.selectByExample(example);
        return new PageInfo<>(imcInspectionTaskList);
    }
    /**
     * 查询当前甲方负责人下全部被否决的巡检任务
     * @param taskQueryDto
     * @return
     */
    @Override
    public List<ImcInspectionTask> getAllDeniedTaskByPrincipalId(TaskQueryDto taskQueryDto){
        PageHelper.startPage(taskQueryDto.getPageNum(),taskQueryDto.getPageSize());
        Long principalId = taskQueryDto.getUserId();
        Example example = new Example(ImcInspectionTask.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("principalId",principalId);
        criteria.andEqualTo("status",TaskStatusEnum.NO_SUCH_STATUS.getStatusNum());
        return imcInspectionTaskMapper.selectByExample(example);
    }

    /**
     * 同意一项巡检任务
     * @param imcTaskChangeStatusDto
     * @return
     */
    @Override
    public ImcTaskChangeStatusDto acceptImcTaskByPrincipal(ImcTaskChangeStatusDto imcTaskChangeStatusDto){
        Long taskId = imcTaskChangeStatusDto.getTaskId();
        LoginAuthDto loginAuthDto = imcTaskChangeStatusDto.getLoginAuthDto();
        ImcInspectionTask imcInspectionTask = this.getTaskByTaskId(taskId);
        Long facilitatorId = imcInspectionTask.getFacilitatorId();
        if(facilitatorId ==null){
            //如果当前巡检任务不存在服务商
            imcInspectionTask.setStatus(TaskStatusEnum.WAITING_FOR_FACILITATOR.getStatusNum());
        }else{//如果存在服务商
            imcInspectionTask.setStatus(TaskStatusEnum.WAITING_FOR_ACCEPT.getStatusNum());
        }
        imcInspectionTask.setUpdateInfo(loginAuthDto);
        this.update(imcInspectionTask);
        int status = imcInspectionTask.getStatus();
        //推送消息
        MqMessageData mqMessageData = taskMsgProducer.sendTaskStatusMsgMq(imcInspectionTask);
        imcTaskManager.modifyTaskStatus(mqMessageData);
        imcTaskChangeStatusDto.setStatus(status);
        imcTaskChangeStatusDto.setStatusMsg(TaskStatusEnum.getStatusMsg(status));
        return imcTaskChangeStatusDto;
    }

    /**
     * 否决一项巡检任务
     * @param imcTaskChangeStatusDto
     * @return
     */
    @Override
    public ImcTaskChangeStatusDto denyImcTaskByPrincipal(ImcTaskChangeStatusDto imcTaskChangeStatusDto){
        Long taskId = imcTaskChangeStatusDto.getTaskId();
        LoginAuthDto loginAuthDto = imcTaskChangeStatusDto.getLoginAuthDto();
        ImcInspectionTask imcInspectionTask = this.getTaskByTaskId(taskId);
        imcInspectionTask.setStatus(TaskStatusEnum.NO_SUCH_STATUS.getStatusNum());
        imcInspectionTask.setUpdateInfo(loginAuthDto);
        this.update(imcInspectionTask);
        int status = imcInspectionTask.getStatus();
        //推送消息
        MqMessageData mqMessageData = taskMsgProducer.sendTaskStatusMsgMq(imcInspectionTask);
        imcTaskManager.modifyTaskStatus(mqMessageData);
        imcTaskChangeStatusDto.setStatus(status);
        imcTaskChangeStatusDto.setStatusMsg(TaskStatusEnum.getStatusMsg(status));
        return imcTaskChangeStatusDto;
    }

    /**
     * 修改巡检任务对应的服务商ID
     * @param taskChangeFacilitatorDto
     * @return
     */
    @Override
    public TaskChangeFacilitatorDto modifyFacilitator(TaskChangeFacilitatorDto taskChangeFacilitatorDto){
        Long taskId = taskChangeFacilitatorDto.getTaskId();
        Long facilitatorId = taskChangeFacilitatorDto.getFacilitatorId();
        Example example = new Example(ImcInspectionTask.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",taskId);
        if(imcInspectionTaskMapper.selectCountByExample(example)==0){
            throw new BusinessException(ErrorCodeEnum.GL9999098);
        }
        ImcInspectionTask imcInspectionTask = this.getTaskByTaskId(taskId);
        imcInspectionTask.setFacilitatorId(facilitatorId);
        int result = this.update(imcInspectionTask);
        if(result == 1){
            return taskChangeFacilitatorDto;
        }
        throw new BusinessException(ErrorCodeEnum.GL9999093);
    }

    /**
     * 服务商拒单
     * @param confirmImcTaskDto
     * @return
     */
    @Override
    public ImcTaskChangeStatusDto refuseImcTaskByFacilitator(ConfirmImcTaskDto confirmImcTaskDto){
        LoginAuthDto loginAuthDto = confirmImcTaskDto.getLoginAuthDto();
        Long taskId = confirmImcTaskDto.getTaskId();
        ImcTaskChangeStatusDto imcTaskChangeStatusDto = new ImcTaskChangeStatusDto();
        imcTaskChangeStatusDto.setStatusMsg(TaskStatusEnum.getStatusMsg(TaskStatusEnum.WAITING_FOR_FACILITATOR.getStatusNum()));
        imcTaskChangeStatusDto.setStatus(TaskStatusEnum.WAITING_FOR_FACILITATOR.getStatusNum());
        imcTaskChangeStatusDto.setTaskId(taskId);
        imcTaskChangeStatusDto.setLoginAuthDto(loginAuthDto);
        Example example = new Example(ImcInspectionTask.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",taskId);
        if(imcInspectionTaskMapper.selectCountByExample(example)==0){
            //如果当前任务不存在
            throw new BusinessException(ErrorCodeEnum.GL9999098);
        }
        ImcInspectionTask imcInspectionTask = imcInspectionTaskMapper.selectByExample(example).get(0);
        if(imcInspectionTask.getStatus().equals(TaskStatusEnum.WAITING_FOR_ACCEPT.getStatusNum())){
            //如果当前任务的状态是等待服务商接单，才允许服务商拒单
            imcInspectionTask.setStatus(TaskStatusEnum.WAITING_FOR_FACILITATOR.getStatusNum());
            imcInspectionTask.setUpdateInfo(loginAuthDto);
            imcInspectionTaskMapper.updateByPrimaryKeySelective(imcInspectionTask);
            //推送消息
            MqMessageData mqMessageData = taskMsgProducer.sendTaskStatusMsgMq(imcInspectionTask);
            imcTaskManager.modifyTaskStatus(mqMessageData);
        }else{
            throw new BusinessException(ErrorCodeEnum.GL9999086);
        }
        return imcTaskChangeStatusDto;
    }

    /**
     * 服务商接单
     * @param confirmImcTaskDto
     * @return
     */
    @Override
    public ImcTaskChangeStatusDto acceptImcTaskByFacilitator(ConfirmImcTaskDto confirmImcTaskDto){
        LoginAuthDto loginAuthDto = confirmImcTaskDto.getLoginAuthDto();
        Long taskId = confirmImcTaskDto.getTaskId();
        ImcTaskChangeStatusDto imcTaskChangeStatusDto = new ImcTaskChangeStatusDto();
        imcTaskChangeStatusDto.setStatusMsg(TaskStatusEnum.getStatusMsg(TaskStatusEnum.EXECUTING.getStatusNum()));
        imcTaskChangeStatusDto.setStatus(TaskStatusEnum.EXECUTING.getStatusNum());
        imcTaskChangeStatusDto.setTaskId(taskId);
        imcTaskChangeStatusDto.setLoginAuthDto(loginAuthDto);
        Example example = new Example(ImcInspectionTask.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",taskId);
        if(imcInspectionTaskMapper.selectCountByExample(example)==0){
            //如果当前任务不存在
            throw new BusinessException(ErrorCodeEnum.GL9999098);
        }
        ImcInspectionTask imcInspectionTask = imcInspectionTaskMapper.selectByExample(example).get(0);
        if(imcInspectionTask.getStatus().equals(TaskStatusEnum.WAITING_FOR_ACCEPT.getStatusNum())){
            //如果当前任务的状态是等待服务商接单，才允许服务商接单
            imcInspectionTask.setStatus(TaskStatusEnum.EXECUTING.getStatusNum());
            imcInspectionTask.setUpdateInfo(loginAuthDto);
            imcInspectionTaskMapper.updateByPrimaryKeySelective(imcInspectionTask);
            //推送消息
            MqMessageData mqMessageData = taskMsgProducer.sendTaskStatusMsgMq(imcInspectionTask);
            imcTaskManager.modifyTaskStatus(mqMessageData);
        }else{
            throw new BusinessException(ErrorCodeEnum.GL9999085);
        }
        return imcTaskChangeStatusDto;
    }

    /**
     * 根据任务id查询任务下的子项数
     * @param taskId
     * @return
     */
    @Override
    public Integer getItemNumberByTaskId(Long taskId){
        if(null!=taskId){
            Example example = new Example(ImcInspectionItem.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("inspectionTaskId",taskId);
            return imcInspectionItemService.selectCountByExample(example);
        }else{
            throw new BusinessException(ErrorCodeEnum.GL99990100);
        }
    }

    /**
     * 获取全部未分配工程师，且还剩3天截止的巡检任务
     * @return
     */
    @Override
    public List<UndistributedImcTaskDto> queryAllUndistributedTask(){
        return imcInspectionTaskMapper.queryAllUndistributedTaskWithTime();
    }

    /**
     * 获取巡检任务报表信息
     * @param taskId
     * @return
     */
    @Override
    public OptUploadFileRespDto generateImcTaskPdf(Long taskId,LoginAuthDto loginAuthDto){
        ImcInspectionTask task = imcInspectionTaskMapper.selectByPrimaryKey(taskId);
        if(null==task) throw new BusinessException(ErrorCodeEnum.GL9999098,taskId);
        List<ImcInspectionTask> list = new ArrayList<>();
        list.add(task);
        //获取巡检任务信息
        ImcInspectionTaskDto imcInspectionTaskDto = this.transform(list).get(0);
        //获取巡检任务对应的全部子项信息
        Example example = new Example(ImcInspectionItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("inspectionTaskId",taskId);
        List<ImcInspectionItem> imcInspectionItems = imcInspectionItemMapper.selectByExample(example);
        List<ImcInspectionItemDto> imcInspectionItemDtos = this.itemTransform(imcInspectionItems);
        ImcTaskReportDto imcTaskReportDto = new ImcTaskReportDto();
        imcTaskReportDto.setImcInspectionTaskDto(imcInspectionTaskDto);
        imcTaskReportDto.setImcInspectionItemDtos(imcInspectionItemDtos);
        return createPdf(imcTaskReportDto,loginAuthDto);
    }

    private OptUploadFileRespDto createPdf(ImcTaskReportDto imcTaskReportDto,LoginAuthDto loginAuthDto){
        OptUploadFileRespDto optUploadFileRespDto = new OptUploadFileRespDto();
        ImcInspectionTaskDto imcInspectionTaskDto = imcTaskReportDto.getImcInspectionTaskDto();
        List<ImcInspectionItemDto> imcInspectionItemDtos = imcTaskReportDto.getImcInspectionItemDtos();
        logger.info("imcTaskReportDto={}",imcTaskReportDto);
        //创建文档对象
        Document document = new Document(PageSize.A4);
        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document,out);

            writer.setPageEvent(new WaterMark("安安运维（北京）科技有限公司"));// 水印

            document.open();
            document.addTitle("安安运维巡检报告");
            //基本文字格式
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titlefont = new Font(bfChinese, 16, Font.BOLD);
            Font headfont = new Font(bfChinese, 14, Font.BOLD);
            Font keyfont = new Font(bfChinese, 10, Font.BOLD);
            Font textfont = new Font(bfChinese, 10, Font.NORMAL);

            //日期转化工具
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // 段落
            Paragraph paragraph = PdfUtil.createParagraph("安安运维巡检报告");

            //直线
            Paragraph p1 = new Paragraph();
            p1.add(new Chunk(new LineSeparator()));

            document.add(paragraph);
            document.add(p1);

            //增添巡检任务表单
            PdfPTable table = PdfUtil.createTable(2,10);
            table.addCell(PdfUtil.createCell("巡检报告：",headfont, Element.ALIGN_LEFT, 6, false));

            table.addCell(PdfUtil.createCell("巡检任务Id", textfont));

            table.addCell(PdfUtil.createCell(String.valueOf(imcInspectionTaskDto.getId()), textfont));

            table.addCell(PdfUtil.createCell("巡检任务名称", textfont));

            table.addCell(PdfUtil.createCell(imcInspectionTaskDto.getTaskName(), textfont));

            table.addCell(PdfUtil.createCell("巡检类型", textfont));

            table.addCell(PdfUtil.createCell(imcInspectionTaskDto.getInspectionType()==1? "计划巡检":"临时巡检", textfont));

            table.addCell(PdfUtil.createCell("项目名称", textfont));

            table.addCell(PdfUtil.createCell(imcInspectionTaskDto.getProjectName(), textfont));

            table.addCell(PdfUtil.createCell("项目负责人姓名", textfont));

            table.addCell(PdfUtil.createCell(imcInspectionTaskDto.getPrincipalName(), textfont));

            table.addCell(PdfUtil.createCell("服务商名称", textfont));

            table.addCell(PdfUtil.createCell(imcInspectionTaskDto.getFacilitatorName(), textfont));

            table.addCell(PdfUtil.createCell("计划起始时间", textfont));

            table.addCell(PdfUtil.createCell(formatter.format(imcInspectionTaskDto.getScheduledStartTime()), textfont));

            table.addCell(PdfUtil.createCell("巡检周期", textfont));

            table.addCell(PdfUtil.createCell(String.valueOf(imcInspectionTaskDto.getFrequency()), textfont));

            table.addCell(PdfUtil.createCell("实际完成时间", textfont));

            table.addCell(PdfUtil.createCell(formatter.format(imcInspectionTaskDto.getActualFinishTime()), textfont));

            table.addCell(PdfUtil.createCell("巡检任务内容", textfont));

            table.addCell(PdfUtil.createCell(imcInspectionTaskDto.getContent(), textfont));

            document.add(table);

            //添加巡检任务子项表
            ImcInspectionItemDto imcInspectionItemDto;
            for(int i=0;i<imcInspectionItemDtos.size();i++){
                imcInspectionItemDto = imcInspectionItemDtos.get(i);
                table = PdfUtil.createTable(2,10);
                table.addCell(PdfUtil.createCell("巡检子项" + (i+1) + ":",headfont, Element.ALIGN_LEFT, 6, false));

                table.addCell(PdfUtil.createCell("巡检子项Id", textfont));

                table.addCell(PdfUtil.createCell(String.valueOf(imcInspectionItemDto.getId()), textfont));

                table.addCell(PdfUtil.createCell("巡检子项名称", textfont));

                table.addCell(PdfUtil.createCell(imcInspectionItemDto.getItemName(), textfont));

                table.addCell(PdfUtil.createCell("巡检网点", textfont));

                table.addCell(PdfUtil.createCell(imcInspectionItemDto.getLocation(), textfont));

                table.addCell(PdfUtil.createCell("计划起始时间", textfont));

                table.addCell(PdfUtil.createCell(formatter.format(imcInspectionItemDto.getScheduledStartTime()), textfont));

                table.addCell(PdfUtil.createCell("计划完成天数", textfont));

                table.addCell(PdfUtil.createCell(String.valueOf(imcInspectionItemDto.getDays()), textfont));

                table.addCell(PdfUtil.createCell("实际起始时间", textfont));

                table.addCell(PdfUtil.createCell(formatter.format(imcInspectionItemDto.getActualStartTime()), textfont));

                table.addCell(PdfUtil.createCell("实际完成时间", textfont));

                table.addCell(PdfUtil.createCell(formatter.format(imcInspectionItemDto.getActualFinishTime()), textfont));

                table.addCell(PdfUtil.createCell("维修工姓名", textfont));

                table.addCell(PdfUtil.createCell(imcInspectionItemDto.getMaintainerName(), textfont));

                table.addCell(PdfUtil.createCell("巡检子项内容", textfont));

                table.addCell(PdfUtil.createCell(imcInspectionItemDto.getDescription(), textfont));

                table.addCell(PdfUtil.createCell("巡检结果描述", textfont));

                table.addCell(PdfUtil.createCell(imcInspectionItemDto.getResult(), textfont));

                document.add(table);
            }

            // 添加图片
            Image image = Image.getInstance("classpath:/static/Logo.png");
            image.setAlignment(Image.ALIGN_CENTER);
            image.scalePercent(40); //依照比例缩放

            document.add(image);

            document.close();

            String filename = "巡检任务报表" + imcInspectionTaskDto.getId() + ".pdf";

            optUploadFileRespDto = uploadReportPdf(out,filename,"pdf",loginAuthDto,imcInspectionTaskDto.getId());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            document.close();
        }
        return optUploadFileRespDto;
    }

    public OptUploadFileRespDto uploadReportPdf(ByteArrayOutputStream out,String fileName,String fileType,LoginAuthDto loginAuthDto,Long taskId){
        Long userId = loginAuthDto.getUserId();
        String userName = loginAuthDto.getUserName();
        OptUploadFileReqDto optUploadFileReqDto = new OptUploadFileReqDto();
        // 将上传文件的字节流封装到到Dto对象中
        OptUploadFileByteInfoReqDto optUploadFileByteInfoReqDto = new OptUploadFileByteInfoReqDto();
        optUploadFileByteInfoReqDto.setFileByteArray(out.toByteArray());
        optUploadFileByteInfoReqDto.setFileName(fileName);
        optUploadFileByteInfoReqDto.setFileType(fileType);
        optUploadFileReqDto.setUploadFileByteInfoReqDto(optUploadFileByteInfoReqDto);

        // 设置不同文件路径来区分pdf
        optUploadFileReqDto.setFilePath("ananops/imc/"+ fileName + "/");
        optUploadFileReqDto.setUserId(userId);
        optUploadFileReqDto.setUserName(userName);
        optUploadFileReqDto.setBucketName("ananops");
        optUploadFileReqDto.setFileType("pdf");
        OptUploadFileRespDto optUploadFileRespDto = opcOssFeignApi.uploadFile(optUploadFileReqDto).getResult();
        logger.info("optUploadFileRespDto={}",optUploadFileRespDto);
        //为附件添加工单号
        Long attachmentId = optUploadFileRespDto.getAttachmentId();
        OptAttachmentUpdateReqDto optAttachmentUpdateReqDto = new OptAttachmentUpdateReqDto();
        optAttachmentUpdateReqDto.setId(attachmentId);
        optAttachmentUpdateReqDto.setLoginAuthDto(loginAuthDto);
        optAttachmentUpdateReqDto.setRefNo(String.valueOf(taskId));
        String result = opcOssFeignApi.updateAttachmentInfo(optAttachmentUpdateReqDto).getResult();
        logger.info("bindResult={}",result);
        //为报表和巡检任务建立绑定关系
        ImcTaskReport imcTaskReport = new ImcTaskReport();
        imcTaskReport.setAttachmentId(attachmentId);
        imcTaskReport.setTaskId(taskId);
        imcTaskReport.setRefNo(String.valueOf(taskId));
        imcTaskReportMapper.insert(imcTaskReport);
        logger.info("imcTaskReport={}",imcTaskReport);
        return optUploadFileRespDto;
    }

    @Override
    public List<ElementImgUrlDto> getReportUrlList(Long taskId,LoginAuthDto loginAuthDto){
        ImcTaskReport imcTaskReport = imcTaskReportMapper.selectByPrimaryKey(taskId);
        if(null == imcTaskReport){
            generateImcTaskPdf(taskId,loginAuthDto);
            imcTaskReport = imcTaskReportMapper.selectByPrimaryKey(taskId);
        }
        String refNo = imcTaskReport.getRefNo();
        OptBatchGetUrlRequest optBatchGetUrlRequest = new OptBatchGetUrlRequest();
        optBatchGetUrlRequest.setRefNo(refNo);
        optBatchGetUrlRequest.setEncrypt(true);
        return opcOssFeignApi.listFileUrl(optBatchGetUrlRequest).getResult();
    }
    /**
     * 判断巡检任务是否完成
     * @param taskId
     * @return
     */
    @Override
    public Boolean isTaskFinish(Long taskId){
        Example example = new Example(ImcInspectionItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("inspectionTaskId",taskId);
        List<ImcInspectionItem> imcInspectionItemList = imcInspectionItemMapper.selectByExample(example);
        for(int i=0;i<imcInspectionItemList.size();i++){
            if(imcInspectionItemList.get(i).getStatus()<ItemStatusEnum.INSPECTION_OVER.getStatusNum()){
                //如果还有巡检任务子项没完成
                return false;
            }
        }
        //如果巡检任务子项都完成了，则巡检任务也完成了
        return true;
    }

    /**
     * 从用户的部门组织Id获取用户所属公司的组织Id
     *
     * @param userGroupId
     *
     * @return
     */
    private Long getCompanyGroupIdFromUserGroupId(Long userGroupId) {
        if (userGroupId == null) {
            throw new UacBizException(ErrorCodeEnum.UAC10015010, userGroupId);
        }
        // 根据组织Id查询公司信息
        CompanyDto companyDto = uacGroupFeignApi.getCompanyInfoById(userGroupId).getResult();
        if (PublicUtil.isEmpty(companyDto)) {
            throw new UacBizException(ErrorCodeEnum.UAC10015001, userGroupId);
        }
        Long facilitatorId = companyDto.getId();
        return facilitatorId;
    }

    private List<ImcInspectionTaskDto> transform(List<ImcInspectionTask> inspectionTasks) {
        List<ImcInspectionTaskDto> imcInspectionTaskDtos = new ArrayList<>();
        Map<Long, String> nameMap = new HashMap<>();
        for (ImcInspectionTask imcInspectionTask : inspectionTasks) {
            ImcInspectionTaskDto inspectionTaskDto = new ImcInspectionTaskDto();
            BeanUtils.copyProperties(imcInspectionTask,inspectionTaskDto);
            // 装入已安排的点位数
            ImcInspectionItem queryItem = new ImcInspectionItem();
            queryItem.setInspectionTaskId(imcInspectionTask.getId());
            List<ImcInspectionItem> items = imcInspectionItemMapper.select(queryItem);
            Integer count = 0;
            if (PublicUtil.isNotEmpty(items)) {
                for (ImcInspectionItem imcInspectionItem : items) {
                    count += imcInspectionItem.getCount();
                }
            }
            inspectionTaskDto.setAlreadyPoint(count);

            // 转换用户名
            Long principalId = imcInspectionTask.getPrincipalId();
            if (nameMap.containsKey(principalId)) {
                inspectionTaskDto.setPrincipalName(nameMap.get(principalId));
            } else {
                UserInfoDto user = uacUserFeignApi.getUacUserById(principalId).getResult();
                if (user != null) {
                    nameMap.put(principalId, user.getUserName());
                    inspectionTaskDto.setPrincipalName(user.getUserName());
                }
            }
            // 转换项目名称
            Long projectId = imcInspectionTask.getProjectId();
            if (nameMap.containsKey(projectId)) {
                inspectionTaskDto.setProjectName(nameMap.get(projectId));
            } else {
                PmcProjectDto projectDto = pmcProjectFeignApi.getProjectByProjectId(projectId).getResult();
                if (projectDto != null) {
                    nameMap.put(projectId, projectDto.getProjectName());
                    inspectionTaskDto.setProjectName(projectDto.getProjectName());
                }
            }
            // 转换服务商名称
            Long facilitatorId = imcInspectionTask.getFacilitatorId();
            if (nameMap.containsKey(facilitatorId)) {
                inspectionTaskDto.setFacilitatorName(nameMap.get(facilitatorId));
            } else {
                GroupSaveDto groupSaveDto = uacGroupFeignApi.getUacGroupById(facilitatorId).getResult();
                if (groupSaveDto != null) {
                    nameMap.put(facilitatorId, groupSaveDto.getGroupName());
                    inspectionTaskDto.setFacilitatorName(groupSaveDto.getGroupName());
                }
            }
            imcInspectionTaskDtos.add(inspectionTaskDto);
        }
        return imcInspectionTaskDtos;
    }


    private List<ImcInspectionItemDto> itemTransform(List<ImcInspectionItem> imcInspectionItems){
        List<ImcInspectionItemDto> imcInspectionItemDtos = new ArrayList<>();
        Map<Long,String> nameMap = new HashMap<>();
        for(ImcInspectionItem imcInspectionItem:imcInspectionItems){
            ImcInspectionItemDto imcInspectionItemDto = new ImcInspectionItemDto();
            BeanUtils.copyProperties(imcInspectionItem,imcInspectionItemDto);
            Long maintainerId = imcInspectionItem.getMaintainerId();
            //转换工程师名称
            if(nameMap.containsKey(maintainerId)){
                imcInspectionItemDto.setMaintainerName(nameMap.get(maintainerId));
            }else{
                UserInfoDto user = uacUserFeignApi.getUacUserById(maintainerId).getResult();
                if (user != null) {
                    nameMap.put(maintainerId, user.getUserName());
                    imcInspectionItemDto.setMaintainerName(user.getUserName());
                }
            }
            imcInspectionItemDtos.add(imcInspectionItemDto);
        }
        return imcInspectionItemDtos;
    }
}
