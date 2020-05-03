package com.ananops.provider.manager;

import com.ananops.provider.annotation.MqProducerStore;
import com.ananops.provider.model.domain.MqMessageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by rongshuai on 2020/2/25 14:43
 */
@Slf4j
@Component
@Transactional(rollbackFor = Exception.class)
public class ImcItemManager {
    @MqProducerStore
    public void modifyItemStatus(final MqMessageData mqMessageData){
        log.info("修改巡检任务子项状态. mqMessageData={}",mqMessageData);
    }
}
