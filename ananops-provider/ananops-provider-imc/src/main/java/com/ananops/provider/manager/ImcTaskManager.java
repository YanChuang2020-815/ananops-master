package com.ananops.provider.manager;


import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.base.exception.BusinessException;
import com.ananops.provider.annotation.MqProducerStore;
import com.ananops.provider.mapper.ImcInspectionTaskMapper;
import com.ananops.provider.model.domain.ImcInspectionTask;
import com.ananops.provider.model.domain.MqMessageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by rongshuai on 2019/12/10 19:34
 */
@Slf4j
@Component
@Transactional(rollbackFor = Exception.class)
public class ImcTaskManager {
    @Resource
    ImcInspectionTaskMapper imcInspectionTaskMapper;

    @MqProducerStore
    public void saveInspectionTask(final MqMessageData mqMessageData, ImcInspectionTask imcInspectionTask, boolean addFlag){
        log.info("保存巡检任务. mqMessageData={}, imcInspectionTask={}",mqMessageData,imcInspectionTask);
        if(addFlag){//如果是巡检任务的添加
            imcInspectionTaskMapper.insert(imcInspectionTask);
        }else{//如果是巡检任务的更新
            int result = imcInspectionTaskMapper.updateByPrimaryKeySelective(imcInspectionTask);
            if(result < 1){
                throw new BusinessException(ErrorCodeEnum.GL9999093);
            }
        }
    }

    @MqProducerStore
    public void modifyTaskStatus(final MqMessageData mqMessageData){
        log.info("修改巡检任务状态. mqMessageData={}",mqMessageData);
    }

    @MqProducerStore
    public void notifyFacilitator(final MqMessageData mqMessageData){
        log.info("通知服务商. mqMessageData={}",mqMessageData);
    }
}
