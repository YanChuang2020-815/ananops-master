package com.ananops.provider.manager;

import com.ananops.provider.annotation.MqProducerStore;
import com.ananops.provider.model.domain.MqMessageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created By ChengHao On 2020/2/29
 */
@Slf4j
@Component
@Transactional(rollbackFor = Exception.class)
public class AmcAlarmManager {

    @MqProducerStore
    public void alarmOccur(final MqMessageData mqMessageData) {
        log.info("报警信息发送,mqMessageData={}", mqMessageData);
    }
}
