package com.ananops.provider.service.impl;


import com.ananops.PublicUtil;
import com.ananops.base.constant.GlobalConstant;
import com.ananops.base.dto.LoginAuthDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.base.exception.BusinessException;
import com.ananops.core.support.BaseService;
import com.ananops.provider.manager.ImcItemManager;
import com.ananops.provider.mapper.*;
import com.ananops.provider.model.domain.*;
import com.ananops.provider.model.dto.*;
import com.ananops.provider.model.dto.attachment.OptAttachmentUpdateReqDto;
import com.ananops.provider.model.dto.attachment.OptUploadFileByteInfoReqDto;
import com.ananops.provider.model.dto.oss.*;
import com.ananops.provider.model.dto.user.UserInfoDto;
import com.ananops.provider.model.enums.ItemStatusEnum;
import com.ananops.provider.model.enums.TaskStatusEnum;
import com.ananops.provider.model.service.UacUserFeignApi;
import com.ananops.provider.mq.producer.ItemMsgProducer;
import com.ananops.provider.service.ImcInspectionItemService;
import com.ananops.provider.service.ImcInspectionTaskService;
import com.ananops.provider.service.MdcFormTemplateFeignApi;
import com.ananops.provider.service.OpcOssFeignApi;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.xiaoleilu.hutool.io.FileTypeUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by rongshuai on 2019/11/28 10:14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ImcInspectionItemServiceImpl extends BaseService<ImcInspectionItem> implements ImcInspectionItemService {
    @Resource
    private ImcInspectionItemMapper imcInspectionItemMapper;

    @Resource
    private ImcInspectionTaskMapper imcInspectionTaskMapper;

    @Resource
    private ImcUserItemMapper imcUserItemMapper;

    @Resource
    private ImcInspectionTaskService imcInspectionTaskService;

    @Resource
    private OpcOssFeignApi opcOssFeignApi;

    @Resource
    private ImcFileTaskItemStatusMapper imcFileTaskItemStatusMapper;

    @Resource
    private ImcItemManager imcItemManager;

    @Resource
    private ItemMsgProducer itemMsgProducer;

    @Resource
    private UacUserFeignApi uacUserFeignApi;

    @Resource
    private MdcFormTemplateFeignApi mdcFormTemplateFeignApi;

    @Resource
    private ImcItemInvoiceMapper imcItemInvoiceMapper;

    @Resource
    private ImcItemInvoiceDescMapper imcItemInvoiceDescMapper;

    @Resource
    private ImcItemInvoiceDeviceMapper imcItemInvoiceDeviceMapper;

    /**
     *
     * @param imcAddInspectionItemDto
     * @return
     */
    public ImcAddInspectionItemDto saveInspectionItem(ImcAddInspectionItemDto imcAddInspectionItemDto, LoginAuthDto loginAuthDto){//编辑巡检任务子项记录
        ImcInspectionItem imcInspectionItem = new ImcInspectionItem();
        BeanUtils.copyProperties(imcAddInspectionItemDto,imcInspectionItem);
        imcInspectionItem.setUpdateInfo(loginAuthDto);
        Long taskId = imcInspectionItem.getInspectionTaskId();
        Long userId = imcAddInspectionItemDto.getUserId();
        Example example = new Example(ImcInspectionTask.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",taskId);
        List<ImcInspectionTask> imcInspectionTasks = imcInspectionTaskMapper.selectByExample(example);
        if(imcInspectionTasks.size()==0){//如果没有此巡检任务
            throw new BusinessException(ErrorCodeEnum.GL9999098,taskId);
        }
        if(imcInspectionItem.isNew()){//如果是新增一条巡检任务子项记录
            Long itemId = super.generateId();
            imcInspectionItem.setId(itemId);
            Long scheduledStartTime = imcInspectionItem.getScheduledStartTime().getTime();
            Long currentTime = System.currentTimeMillis();
            if(scheduledStartTime<=currentTime){
                //如果计划执行时间<=当前时间，说明，巡检任务需要立即执行
                //将巡检任务子项的状态设置为等待分配工程师
                imcInspectionItem.setStatus(ItemStatusEnum.WAITING_FOR_MAINTAINER.getStatusNum());
            }

            //新建任务子项的时候根据点位数量创建需要提交的巡检单据
            Integer count = imcAddInspectionItemDto.getCount();
            if (count != null && count>0) {
                // 使用子项中的Result字段来保存待填写的巡检表单数，均填写完成后写入finish字符串表示已完成
                imcInspectionItem.setResult(String.valueOf(count));
                Long projectId = imcInspectionTasks.get(0).getProjectId();
                FormTemplateDto formTemplateDto = mdcFormTemplateFeignApi.getFormTemplateByProjectId(projectId).getResult();
                if (formTemplateDto == null) {
                    throw new BusinessException(ErrorCodeEnum.GL99990006,projectId);
                }
                for (int i=0; i<count; i++) {
                    Long invoiceId = super.generateId();
                    ImcItemInvoice imcItemInvoice = new ImcItemInvoice();
                    imcItemInvoice.setId(invoiceId);
                    imcItemInvoice.setTemplateId(formTemplateDto.getId());
                    imcItemInvoice.setInspcItemId(itemId);
                    imcItemInvoice.setStatus("N");
                    imcItemInvoice.setDr("0");
                    imcItemInvoice.setUpdateInfo(loginAuthDto);
                    imcItemInvoiceMapper.insert(imcItemInvoice);
                    // 创建资产清单部分
                    List<FormTemplateItemDto> assetList = formTemplateDto.getAssetList();
                    if (assetList != null) {
                        for (FormTemplateItemDto formTemplateItemDto : assetList) {
                            Long deviceId = super.generateId();
                            ImcItemInvoiceDevice imcItemInvoiceDevice = new ImcItemInvoiceDevice();
                            imcItemInvoiceDevice.setUpdateInfo(loginAuthDto);
                            imcItemInvoiceDevice.setId(deviceId);
                            imcItemInvoiceDevice.setInvoiceId(invoiceId);
                            imcItemInvoiceDevice.setDevice(formTemplateItemDto.getContent());
                            imcItemInvoiceDevice.setSort(formTemplateItemDto.getSort());
                            imcItemInvoiceDeviceMapper.insert(imcItemInvoiceDevice);
                        }
                    }
                    // 创建巡检内容项部分
                    List<FormTemplateItemDto> inspcDetailList = formTemplateDto.getInspcDetailList();
                    if (inspcDetailList != null) {
                        for (FormTemplateItemDto formItemDto : inspcDetailList) {
                            Long descId = super.generateId();
                            ImcItemInvoiceDesc imcItemInvoiceDesc = new ImcItemInvoiceDesc();
                            imcItemInvoiceDesc.setUpdateInfo(loginAuthDto);
                            imcItemInvoiceDesc.setId(descId);
                            imcItemInvoiceDesc.setInvoiceId(invoiceId);
                            imcItemInvoiceDesc.setItemContent(formItemDto.getContent());
                            imcItemInvoiceDesc.setSort(formItemDto.getSort());
                            imcItemInvoiceDescMapper.insert(imcItemInvoiceDesc);
                        }
                    }
                }
            }
            // 巡检单据创建完成之后再创建任务子项。
            imcInspectionItemMapper.insert(imcInspectionItem);

            //新增一条巡检任务子项和甲方用户的关系记录
            ImcUserItem imcUserItem = new ImcUserItem();
            imcUserItem.setItemId(itemId);
            imcUserItem.setUserId(userId);
            imcUserItemMapper.insert(imcUserItem);
            List<Long> attachmentIds = imcAddInspectionItemDto.getAttachmentIds();
            if(attachmentIds != null){
                String refNo = String.valueOf(super.generateId());
                int[] statusList = {
                        ItemStatusEnum.WAITING_FOR_MAINTAINER.getStatusNum(),
                        ItemStatusEnum.WAITING_FOR_ACCEPT.getStatusNum(),
                        ItemStatusEnum.IN_THE_INSPECTION.getStatusNum()};
                for(Long attachmentId:attachmentIds){
                    //建立附件与巡检任务、任务子项、当前状态的关联关系
                    logger.info("attachementId={}",attachmentId);
                    this.bindImcItemAndFiles(attachmentId,taskId,itemId,refNo,statusList,loginAuthDto);
                }
            }
            //推送消息
            MqMessageData mqMessageData = itemMsgProducer.sendItemStatusMsgMq(imcInspectionItem);
            imcItemManager.modifyItemStatus(mqMessageData);
        }else{//如果是更新已经存在的巡检任务子项
            imcInspectionItemMapper.updateByPrimaryKeySelective(imcInspectionItem);
        }
        BeanUtils.copyProperties(imcInspectionItem,imcAddInspectionItemDto);

        return imcAddInspectionItemDto;
    }

    public void deleteItemByItemId(Long itemId){
        Example example = new Example(ImcInspectionItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",itemId);
        imcInspectionItemMapper.deleteByExample(example);
    }

    /**
     *
     * @param itemQueryDto
     * @return
     */
    @Override
    public PageInfo getAllItemByTaskIdAndPage(ItemQueryDto itemQueryDto){
        Long taskId = itemQueryDto.getTaskId();
        Example example1 = new Example(ImcInspectionTask.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("id",taskId);
        if(imcInspectionTaskMapper.selectCountByExample(example1)==0){
            //如果查询的任务不存在
            throw new BusinessException(ErrorCodeEnum.GL9999098,taskId);
        }
        Example example2 = new Example(ImcInspectionItem.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andEqualTo("inspectionTaskId",taskId);
        Page page = PageHelper.startPage(itemQueryDto.getPageNum(),itemQueryDto.getPageSize());
        example2.setOrderByClause("update_time DESC");
        List<ImcInspectionItem> imcInspectionItems = imcInspectionItemMapper.selectByExample(example2);
        PageInfo pageInfo = new PageInfo<>(itemTransform(imcInspectionItems));
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPages(page.getPages());
        return pageInfo;
    }
    /**
     *
     * @param itemQueryDto
     * @return
     */
    public List<ImcInspectionItem> getAllItemByTaskId(ItemQueryDto itemQueryDto){
        Long taskId = itemQueryDto.getTaskId();
        Example example1 = new Example(ImcInspectionTask.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("id",taskId);
        if(imcInspectionTaskMapper.selectCountByExample(example1)==0){
            //如果查询的任务不存在
            throw new BusinessException(ErrorCodeEnum.GL9999098,taskId);
        }
        Example example2 = new Example(ImcInspectionItem.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andEqualTo("inspectionTaskId",taskId);
        PageHelper.startPage(itemQueryDto.getPageNum(),itemQueryDto.getPageSize());
        List<ImcInspectionItem> imcInspectionItems = imcInspectionItemMapper.selectByExample(example2);
        return imcInspectionItems;
    }

    @Override
    public PageInfo getAllItemByTaskIdAndStatusAndPage(ItemQueryDto itemQueryDto){
        Long taskId = itemQueryDto.getTaskId();
        int status = itemQueryDto.getStatus();
        Example example1 = new Example(ImcInspectionTask.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("id",taskId);
        if(imcInspectionTaskMapper.selectCountByExample(example1)==0){
            //如果查询的任务不存在
            throw new BusinessException(ErrorCodeEnum.GL9999098,taskId);
        }
        Example example2 = new Example(ImcInspectionItem.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andEqualTo("inspectionTaskId",taskId);
        criteria2.andEqualTo("status",status);
        example2.setOrderByClause("update_time DESC");
        PageHelper.startPage(itemQueryDto.getPageNum(),itemQueryDto.getPageSize());
        List<ImcInspectionItem> imcInspectionItems = imcInspectionItemMapper.selectByExample(example2);
        return new PageInfo<>(imcInspectionItems);
    }

    @Override
    public List<ImcInspectionItem> getAllItemByTaskIdAndStatus(ItemQueryDto itemQueryDto){
        Long taskId = itemQueryDto.getTaskId();
        int status = itemQueryDto.getStatus();
        Example example1 = new Example(ImcInspectionTask.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("id",taskId);
        if(imcInspectionTaskMapper.selectCountByExample(example1)==0){
            //如果查询的任务不存在
            throw new BusinessException(ErrorCodeEnum.GL9999098,taskId);
        }
        Example example2 = new Example(ImcInspectionItem.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andEqualTo("inspectionTaskId",taskId);
        criteria2.andEqualTo("status",status);
        PageHelper.startPage(itemQueryDto.getPageNum(),itemQueryDto.getPageSize());
        List<ImcInspectionItem> imcInspectionItems = imcInspectionItemMapper.selectByExample(example2);
        return imcInspectionItems;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public ImcInspectionItem getItemByItemId(Long itemId){
        Example example = new Example(ImcInspectionItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",itemId);
        if(imcInspectionItemMapper.selectCountByExample(example)==0){
            throw new BusinessException(ErrorCodeEnum.GL9999097,itemId);
        }
        return imcInspectionItemMapper.selectByExample(example).get(0);
    }

    @Override
    public ImcInspectionItemDto getItemDtoByItemId(Long itemId) {
        Example example = new Example(ImcInspectionItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",itemId);
        if(imcInspectionItemMapper.selectCountByExample(example)==0){
            throw new BusinessException(ErrorCodeEnum.GL9999097,itemId);
        }
        List<ImcInspectionItem> items = imcInspectionItemMapper.selectByExample(example);
        return itemTransform(items).get(0);
    }

    public List<ImcInspectionItem> getItemByItemStatusAndTaskId(ItemQueryDto itemQueryDto){
        Long taskId = itemQueryDto.getTaskId();
        Integer status = itemQueryDto.getStatus();
        Example example = new Example(ImcInspectionItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("inspectionTaskId",taskId);
        if(imcInspectionItemMapper.selectCountByExample(example)==0){
            throw new BusinessException(ErrorCodeEnum.GL9999098);
        }
        criteria.andEqualTo("status",status);
        if(imcInspectionItemMapper.selectCountByExample(example)==0){
            throw new BusinessException(ErrorCodeEnum.GL9999090);
        }
        PageHelper.startPage(itemQueryDto.getPageNum(),itemQueryDto.getPageSize());
        return imcInspectionItemMapper.selectByExample(example);
    }

    @Override
    public PageInfo getItemByUserIdAndPage(ItemQueryDto itemQueryDto){
        PageHelper.startPage(itemQueryDto.getPageNum(),itemQueryDto.getPageSize());
        List<ImcInspectionItem> imcInspectionItems = imcInspectionItemMapper.queryItemByUserId(itemQueryDto.getUserId());
        return new PageInfo<>(imcInspectionItems);
    }

    @Override
    public List<ImcInspectionItem> getItemByUserId(ItemQueryDto itemQueryDto){
        PageHelper.startPage(itemQueryDto.getPageNum(),itemQueryDto.getPageSize());
        return imcInspectionItemMapper.queryItemByUserId(itemQueryDto.getUserId());
    }

    @Override
    public PageInfo getItemByUserIdAndStatusAndPage(ItemQueryDto itemQueryDto){
        PageHelper.startPage(itemQueryDto.getPageNum(),itemQueryDto.getPageSize());
        List<ImcInspectionItem> imcInspectionItems = imcInspectionItemMapper.queryItemByUserIdAndStatus(itemQueryDto.getUserId(),itemQueryDto.getStatus());
        return new PageInfo<>(imcInspectionItems);
    }
    @Override
    public List<ImcInspectionItem> getItemByUserIdAndStatus(ItemQueryDto itemQueryDto){
        PageHelper.startPage(itemQueryDto.getPageNum(),itemQueryDto.getPageSize());
        return imcInspectionItemMapper.queryItemByUserIdAndStatus(itemQueryDto.getUserId(),itemQueryDto.getStatus());
    }

    @Override
    public PageInfo getItemByMaintainerIdAndPage(ItemQueryDto itemQueryDto){
        Long maintainerId = itemQueryDto.getMaintainerId();
        Example example = new Example(ImcInspectionItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("maintainerId",maintainerId);
        example.setOrderByClause("update_time DESC");
        PageHelper.startPage(itemQueryDto.getPageNum(),itemQueryDto.getPageSize());
        List<ImcInspectionItem> imcInspectionItems = imcInspectionItemMapper.selectByExample(example);
        return new PageInfo<>(imcInspectionItems);
    }
    @Override
    public List<ImcInspectionItem> getItemByMaintainerId(ItemQueryDto itemQueryDto){
        Long maintainerId = itemQueryDto.getMaintainerId();
        Example example = new Example(ImcInspectionItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("maintainerId",maintainerId);
        PageHelper.startPage(itemQueryDto.getPageNum(),itemQueryDto.getPageSize());
        return imcInspectionItemMapper.selectByExample(example);
    }

    @Override
    public PageInfo getItemByMaintainerIdAndStatusAndPage(ItemQueryDto itemQueryDto){
        Long maintainerId = itemQueryDto.getMaintainerId();
        Integer status = itemQueryDto.getStatus();
        Example example = new Example(ImcInspectionItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("maintainerId",maintainerId);
        criteria.andEqualTo("status",status);
        example.setOrderByClause("update_time DESC");
        PageHelper.startPage(itemQueryDto.getPageNum(),itemQueryDto.getPageSize());
        List<ImcInspectionItem> imcInspectionItems = imcInspectionItemMapper.selectByExample(example);
        return new PageInfo<>(imcInspectionItems);
    }
    @Override
    public List<ImcInspectionItem> getItemByMaintainerIdAndStatus(ItemQueryDto itemQueryDto){
        Long maintainerId = itemQueryDto.getMaintainerId();
        Integer status = itemQueryDto.getStatus();
        Example example = new Example(ImcInspectionItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("maintainerId",maintainerId);
        criteria.andEqualTo("status",status);
        PageHelper.startPage(itemQueryDto.getPageNum(),itemQueryDto.getPageSize());
        return imcInspectionItemMapper.selectByExample(example);
    }

    @Override
    public PageInfo getAllFinishedItemByMaintainerId(ItemQueryDto itemQueryDto){
        Long maintainerId = itemQueryDto.getMaintainerId();
        PageHelper.startPage(itemQueryDto.getPageNum(),itemQueryDto.getPageSize());
        List<ImcInspectionItem> imcInspectionItems = imcInspectionItemMapper.queryFinishedItemByMaintainerId(maintainerId);
        return new PageInfo<>(imcInspectionItems);
    }

    /**
     * 修改巡检任务子项对应的维修工ID
     * @param itemChangeMaintainerDto
     * @return
     */
    public ItemChangeMaintainerDto modifyMaintainerIdByItemId(ItemChangeMaintainerDto itemChangeMaintainerDto){
        Long itemId = itemChangeMaintainerDto.getItemId();
        Long maintainerId = itemChangeMaintainerDto.getMaintainerId();
        Example example = new Example(ImcInspectionItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",itemId);
        if(imcInspectionItemMapper.selectCountByExample(example)==0){
            throw new BusinessException(ErrorCodeEnum.GL9999097);
        }
        ImcInspectionItem imcInspectionItem = this.getItemByItemId(itemId);
        imcInspectionItem.setMaintainerId(maintainerId);
        int result = this.update(imcInspectionItem);
        if(result == 1){
            return itemChangeMaintainerDto;
        }
        throw new BusinessException(ErrorCodeEnum.GL9999093);
    }

    /**
     * 工程师拒单（巡检任务子项）
     * @param confirmImcItemDto
     * @return
     */
    public ImcItemChangeStatusDto refuseImcItemByItemId(ConfirmImcItemDto confirmImcItemDto){
        LoginAuthDto loginAuthDto = confirmImcItemDto.getLoginAuthDto();
        Long itemId = confirmImcItemDto.getItemId();
        ImcItemChangeStatusDto imcItemChangeStatusDto = new ImcItemChangeStatusDto();
        imcItemChangeStatusDto.setLoginAuthDto(loginAuthDto);
        imcItemChangeStatusDto.setItemId(itemId);
        imcItemChangeStatusDto.setStatus(ItemStatusEnum.WAITING_FOR_MAINTAINER.getStatusNum());
        imcItemChangeStatusDto.setStatusMsg(ItemStatusEnum.WAITING_FOR_MAINTAINER.getStatusMsg());
        Example example = new Example(ImcInspectionItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",itemId);
        if(imcInspectionItemMapper.selectCountByExample(example)==0){
            //如果当前任务子项不存在
            throw new BusinessException(ErrorCodeEnum.GL9999097);
        }
        ImcInspectionItem imcInspectionItem = imcInspectionItemMapper.selectByExample(example).get(0);
        if(imcInspectionItem.getStatus().equals(ItemStatusEnum.WAITING_FOR_ACCEPT.getStatusNum())){
            //如果当前任务的状态是等待工程师接单，才允许工程师拒单
            imcInspectionItem.setStatus(ItemStatusEnum.WAITING_FOR_MAINTAINER.getStatusNum());
            imcInspectionItem.setUpdateInfo(loginAuthDto);
            imcInspectionItemMapper.updateByPrimaryKeySelective(imcInspectionItem);
            //推送消息
            MqMessageData mqMessageData = itemMsgProducer.sendItemStatusMsgMq(imcInspectionItem);
            imcItemManager.modifyItemStatus(mqMessageData);
        }else{
            throw new BusinessException(ErrorCodeEnum.GL9999087);
        }
        return imcItemChangeStatusDto;
    }

    /**
     * 工程师接单
     * @param confirmImcItemDto
     * @return
     */
    @Override
    public ImcItemChangeStatusDto acceptImcItemByItemId(ConfirmImcItemDto confirmImcItemDto){
        LoginAuthDto loginAuthDto = confirmImcItemDto.getLoginAuthDto();
        Long itemId = confirmImcItemDto.getItemId();
        ImcItemChangeStatusDto imcItemChangeStatusDto = new ImcItemChangeStatusDto();
        imcItemChangeStatusDto.setLoginAuthDto(loginAuthDto);
        imcItemChangeStatusDto.setItemId(itemId);
        imcItemChangeStatusDto.setStatus(ItemStatusEnum.IN_THE_INSPECTION.getStatusNum());
        imcItemChangeStatusDto.setStatusMsg(ItemStatusEnum.IN_THE_INSPECTION.getStatusMsg());
        Example example = new Example(ImcInspectionItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",itemId);
        if(imcInspectionItemMapper.selectCountByExample(example)==0){
            //如果当前任务子项不存在
            throw new BusinessException(ErrorCodeEnum.GL9999097);
        }
        ImcInspectionItem imcInspectionItem = imcInspectionItemMapper.selectByExample(example).get(0);
        if(imcInspectionItem.getStatus().equals(ItemStatusEnum.WAITING_FOR_ACCEPT.getStatusNum())){
            //如果当前任务的状态是等待工程师接单，才允许工程师接单
            imcInspectionItem.setStatus(ItemStatusEnum.IN_THE_INSPECTION.getStatusNum());
            imcInspectionItem.setUpdateInfo(loginAuthDto);
            // 这里注释掉实际开始时间，接单之后，不一定开始。实际开始时候巡检任务执行的时候自行填写
//            imcInspectionItem.setActualStartTime(new Date(System.currentTimeMillis()));
            imcInspectionItemMapper.updateByPrimaryKeySelective(imcInspectionItem);
            //推送消息
            MqMessageData mqMessageData = itemMsgProducer.sendItemStatusMsgMq(imcInspectionItem);
            imcItemManager.modifyItemStatus(mqMessageData);
        }else{
            throw new BusinessException(ErrorCodeEnum.GL9999087);
        }
        return imcItemChangeStatusDto;
    }

    @Override
    public ImcItemChangeStatusDto modifyImcItemStatusByItemId(ImcItemChangeStatusDto imcItemChangeStatusDto){
        imcItemChangeStatusDto.setStatusMsg(ItemStatusEnum.getStatusMsg(imcItemChangeStatusDto.getStatus()));
        Long itemId = imcItemChangeStatusDto.getItemId();
        int status = imcItemChangeStatusDto.getStatus();
        LoginAuthDto loginAuthDto = imcItemChangeStatusDto.getLoginAuthDto();
        Example example = new Example(ImcInspectionItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",itemId);
        if(this.selectCountByExample(example)==0){
            //如果当前巡检任务子项不存在
            throw new BusinessException(ErrorCodeEnum.GL9999097);
        }
        //如果当前巡检任务子项存在
        ImcInspectionItem imcInspectionItem = new ImcInspectionItem();
        imcInspectionItem.setId(itemId);
        imcInspectionItem.setStatus(status);
        imcInspectionItem.setUpdateInfo(loginAuthDto);
        Long taskId = this.getItemByItemId(itemId).getInspectionTaskId();
        if(status == ItemStatusEnum.IN_THE_INSPECTION.getStatusNum()){
            //如果工程师已接单，巡检任务开始
            System.out.println("++++工程师已接单");
            imcInspectionItem.setActualStartTime(new Date(System.currentTimeMillis()));
            this.update(imcInspectionItem);//更新当前巡检任务子项的状态
        }
        else if(status==ItemStatusEnum.INSPECTION_OVER.getStatusNum()){
            imcInspectionItem.setActualFinishTime(new Date(System.currentTimeMillis()));//设置任务子项的实际完成时间
            this.update(imcInspectionItem);//更新当前巡检任务子项的状态
            List<Long> attachmentIds = imcItemChangeStatusDto.getAttachmentIds();
            String refNo = String.valueOf(super.generateId());
            int[] statusList = {
                    ItemStatusEnum.INSPECTION_OVER.getStatusNum()};
            if(attachmentIds!=null){
                for(Long attachmentId:attachmentIds){
                    //建立附件与巡检任务、任务子项、当前状态的关联关系
                    this.bindImcItemAndFiles(attachmentId,taskId,itemId,refNo,statusList,loginAuthDto);
                }
            }
            if(imcInspectionTaskService.isTaskFinish(taskId)){
                //如果该巡检子项对应的巡检任务中全部的任务子项均已完成
                //则修改对应的巡检任务状态为已完成
                ImcTaskChangeStatusDto imcTaskChangeStatusDto = new ImcTaskChangeStatusDto();
                imcTaskChangeStatusDto.setTaskId(taskId);
                imcTaskChangeStatusDto.setStatus(TaskStatusEnum.WAITING_FOR_CONFIRM.getStatusNum());//将巡检任务状态修改为“巡检结果待审核”
                imcInspectionTaskService.modifyTaskStatus(imcTaskChangeStatusDto,loginAuthDto);
            }
        }
        else{
            this.update(imcInspectionItem);//更新当前巡检任务子项的状态
        }
        //推送消息
        imcInspectionItem = imcInspectionItemMapper.selectByPrimaryKey(itemId);
        MqMessageData mqMessageData = itemMsgProducer.sendItemStatusMsgMq(imcInspectionItem);
        imcItemManager.modifyItemStatus(mqMessageData);
        return imcItemChangeStatusDto;
    }

    @Override
    public PageInfo getAcceptedItemOfMaintainerAndPage(ItemQueryDto itemQueryDto){
        Long maintainerId = itemQueryDto.getMaintainerId();
        Example example = new Example(ImcInspectionItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("maintainerId",maintainerId);
        PageHelper.startPage(itemQueryDto.getPageNum(),itemQueryDto.getPageSize());
        example.setOrderByClause("update_time DESC");
        List<ImcInspectionItem> imcInspectionItems = imcInspectionItemMapper.selectByExample(example);
        List<ImcInspectionItem> imcInspectionItemsResult = new ArrayList<>();
        imcInspectionItems.forEach(imcInspectionItem -> {
            int status = imcInspectionItem.getStatus();
            if(status!=ItemStatusEnum.WAITING_FOR_MAINTAINER.getStatusNum() && status != ItemStatusEnum.WAITING_FOR_ACCEPT.getStatusNum() && status != ItemStatusEnum.INSPECTION_OVER.getStatusNum() && status != ItemStatusEnum.VERIFIED.getStatusNum()){
                imcInspectionItemsResult.add(imcInspectionItem);
            }
        });
        return new PageInfo<>(imcInspectionItemsResult);
    }
    @Override
    public List<ImcInspectionItem> getAcceptedItemOfMaintainer(ItemQueryDto itemQueryDto){
        Long maintainerId = itemQueryDto.getMaintainerId();
        Example example = new Example(ImcInspectionItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("maintainerId",maintainerId);
        example.setOrderByClause("update_time DESC");
        PageHelper.startPage(itemQueryDto.getPageNum(),itemQueryDto.getPageSize());
        List<ImcInspectionItem> imcInspectionItems = imcInspectionItemMapper.selectByExample(example);
        List<ImcInspectionItem> imcInspectionItemsResult = new ArrayList<>();
        imcInspectionItems.forEach(imcInspectionItem -> {
            int status = imcInspectionItem.getStatus();
            if(status!=ItemStatusEnum.WAITING_FOR_MAINTAINER.getStatusNum() && status != ItemStatusEnum.WAITING_FOR_ACCEPT.getStatusNum() && status != ItemStatusEnum.INSPECTION_OVER.getStatusNum() && status != ItemStatusEnum.VERIFIED.getStatusNum()){
                imcInspectionItemsResult.add(imcInspectionItem);
            }
        });
        return imcInspectionItemsResult;
    }

    @Override
    public List<OptUploadFileRespDto> uploadImcItemFile(MultipartHttpServletRequest multipartRequest, OptUploadFileReqDto optUploadFileReqDto, LoginAuthDto loginAuthDto){
        // 这里的filePath来区分照片类型:
        // /ananops/imc/userId/filePath/
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
                optUploadFileReqDto.setFilePath("ananops/imc/" + userId + "/" + filePath + "/");
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
    public List<ElementImgUrlDto> getImcItemFileList(ImcPicQueryDto imcPicQueryDto){
        Long taskId = imcPicQueryDto.getTaskId();
        Long itemId = imcPicQueryDto.getItemId();
        int itemStatus = imcPicQueryDto.getItemStatus();
        Example example = new Example(ImcFileTaskItemStatus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("taskId",taskId);
        criteria.andEqualTo("itemId",itemId);
        criteria.andEqualTo("itemStatus",itemStatus);
        List<ImcFileTaskItemStatus> imcFileTaskItemStatusList = imcFileTaskItemStatusMapper.selectByExample(example);
        if(imcFileTaskItemStatusList.size()>0){
            String refNo = imcFileTaskItemStatusList.get(0).getRefNo();
            OptBatchGetUrlRequest optBatchGetUrlRequest = new OptBatchGetUrlRequest();
            optBatchGetUrlRequest.setRefNo(refNo);
            optBatchGetUrlRequest.setEncrypt(true);
            return opcOssFeignApi.listFileUrl(optBatchGetUrlRequest).getResult();
        }else{
            throw new BusinessException(ErrorCodeEnum.IMC10090005);
        }
    }

    @Override
    public List<ImcItemUrlDto> getAllImcItemPicList(ImcPicQueryDto imcPicQueryDto)
    {
        Long taskId = imcPicQueryDto.getTaskId();
        Long itemId = imcPicQueryDto.getItemId();
        Example example = new Example(ImcFileTaskItemStatus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("taskId",taskId);
        criteria.andEqualTo("itemId",itemId);
        List<ImcFileTaskItemStatus> imcFileTaskItemStatusList = imcFileTaskItemStatusMapper.selectByExample(example);
        HashSet<Integer> set = new HashSet<>();
        List<ImcItemUrlDto> imcItemUrlDtoList = new ArrayList<>();
        imcFileTaskItemStatusList.forEach(item->{
            int itemStatus = item.getItemStatus();
            if(!set.contains(itemStatus)){
                set.add(itemStatus);
                String refNo = item.getRefNo();
                OptBatchGetUrlRequest optBatchGetUrlRequest = new OptBatchGetUrlRequest();
                optBatchGetUrlRequest.setRefNo(refNo);
                optBatchGetUrlRequest.setEncrypt(true);
                List<ElementImgUrlDto> elementImgUrlDtoList = opcOssFeignApi.listFileUrl(optBatchGetUrlRequest).getResult();
                ImcItemUrlDto imcItemUrlDto = new ImcItemUrlDto();
                imcItemUrlDto.setItemId(itemId);
                imcItemUrlDto.setTaskId(taskId);
                imcItemUrlDto.setItemStatus(itemStatus);
                imcItemUrlDto.setElementImgUrlDtos(elementImgUrlDtoList);
                imcItemUrlDtoList.add(imcItemUrlDto);
            }
        });
        return imcItemUrlDtoList;
    }

    /**
     * 为某个状态下的工单绑定图片列表
     * @param attachmentId
     * @param taskId
     * @param itemId
     * @param refNo
     * @param statusList
     * @param loginAuthDto
     */
    public void bindImcItemAndFiles(Long attachmentId,Long taskId,Long itemId,String refNo,int[] statusList,LoginAuthDto loginAuthDto){
        //建立附件与巡检任务、任务子项、当前状态的关联关系
        ImcFileTaskItemStatus imcFileTaskItemStatus = new ImcFileTaskItemStatus();
        imcFileTaskItemStatus.setAttachmentId(attachmentId);
        imcFileTaskItemStatus.setTaskId(taskId);
        imcFileTaskItemStatus.setItemId(itemId);
        imcFileTaskItemStatus.setRefNo(refNo);
        for(Integer status:statusList){
            imcFileTaskItemStatus.setItemStatus(status);
            imcFileTaskItemStatusMapper.insert(imcFileTaskItemStatus);
        }
        //为附件添加工单号
        OptAttachmentUpdateReqDto optAttachmentUpdateReqDto = new OptAttachmentUpdateReqDto();
        optAttachmentUpdateReqDto.setId(attachmentId);
        optAttachmentUpdateReqDto.setLoginAuthDto(loginAuthDto);
        optAttachmentUpdateReqDto.setRefNo(refNo);
        opcOssFeignApi.updateAttachmentInfo(optAttachmentUpdateReqDto);
    }

    /**
     * 根据任务Id查询对应的子项的数目
     * @param taskId
     * @return
     */
    @Override
    public Integer getImcItemNumberByTaskId(Long taskId){
        if(taskId==null){
            throw new BusinessException(ErrorCodeEnum.GL99990100);
        }
        Example example = new Example(ImcInspectionItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("inspectionTaskId",taskId);
        return imcInspectionItemMapper.selectCountByExample(example);
    }
    public Integer setBasicInfoFromContract(){//将从合同中获取到的基本信息填写到巡检任务中
        return 1;
    }

    private List<ImcInspectionItemDto> itemTransform(List<ImcInspectionItem> imcInspectionItems){
        List<ImcInspectionItemDto> imcInspectionItemDtos = new ArrayList<>();
        Map<Long,String> nameMap = new HashMap<>();
        for(ImcInspectionItem imcInspectionItem:imcInspectionItems){
            ImcInspectionItemDto imcInspectionItemDto = new ImcInspectionItemDto();
            BeanUtils.copyProperties(imcInspectionItem,imcInspectionItemDto);
            Long maintainerId = imcInspectionItem.getMaintainerId();
            //转换工程师名称
            if (maintainerId != null) {
                if(nameMap.containsKey(maintainerId)){
                    imcInspectionItemDto.setMaintainerName(nameMap.get(maintainerId));
                }else{
                    UserInfoDto user = uacUserFeignApi.getUacUserById(maintainerId).getResult();
                    if (user != null) {
                        nameMap.put(maintainerId, user.getUserName());
                        imcInspectionItemDto.setMaintainerName(user.getUserName());
                    }
                }
            }
            imcInspectionItemDtos.add(imcInspectionItemDto);
        }
        return imcInspectionItemDtos;
    }

    @Override
    public void handleInvoice(ItemChangeMaintainerDto itemChangeMaintainerDto) {
        Long itemId = itemChangeMaintainerDto.getItemId();
        Long maintainerId = itemChangeMaintainerDto.getMaintainerId();
        // 根据子项任务Id查找相关的单据列表
        Example example = new Example(ImcItemInvoice.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("inspcItemId",itemId);
        List<ImcItemInvoice> imcItemInvoices = imcItemInvoiceMapper.selectByExample(example);
        if (imcItemInvoices != null) {
            // 查询工程师信息，工程师是直接挂在公司组织下的，未分部门，所以这里的工程师组织Id就是公司组织Id，组织名称就是公司名称
            UserInfoDto user = uacUserFeignApi.getUacUserById(maintainerId).getResult();
            for (ImcItemInvoice imcItemInvoice : imcItemInvoices) {
                imcItemInvoice.setInspcCompanyId(user.getGroupId());
                imcItemInvoice.setInspcCompany(user.getGroupName());
                imcItemInvoice.setEngineerId(maintainerId);
                imcItemInvoice.setEngineer(user.getUserName());
                imcItemInvoiceMapper.updateByPrimaryKeySelective(imcItemInvoice);
            }
        }
    }

    @Override
    public ImcItemChangeStatusDto putResultByItemId(ItemResultDto itemResultDto, LoginAuthDto loginAuthDto) {
        // 增量更新子项实际工作起始时间
        ImcInspectionItem imcInspectionItem = new ImcInspectionItem();
        imcInspectionItem.setId(itemResultDto.getItemId());
        imcInspectionItem.setActualStartTime(itemResultDto.getActualStartTime());
        imcInspectionItem.setActualFinishTime(itemResultDto.getActualFinishTime());
        imcInspectionItemMapper.updateByPrimaryKeySelective(imcInspectionItem);
        // 走原接口逻辑
        ImcItemChangeStatusDto imcItemChangeStatusDto = new ImcItemChangeStatusDto();
        BeanUtils.copyProperties(itemResultDto, imcItemChangeStatusDto);
        imcItemChangeStatusDto.setLoginAuthDto(loginAuthDto);
        return this.modifyImcItemStatusByItemId(imcItemChangeStatusDto);
    }
}
