package com.ananops.provider.manager;

import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.base.exception.BusinessException;
import com.ananops.provider.annotation.MqProducerStore;
import com.ananops.provider.mapper.MdmcTaskMapper;
import com.ananops.provider.model.domain.MdmcTask;
import com.ananops.provider.model.domain.MqMessageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Slf4j
@Component
@Transactional(rollbackFor = Exception.class)
public class MdmcTaskManager {
    @Resource
    MdmcTaskMapper taskMapper;

    @MqProducerStore
    public void modifyTaskStatus(final MqMessageData mqMessageData){
        log.info("修改维修工单状态. mqMessageData={}",mqMessageData);
    }



}
