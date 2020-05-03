package com.ananops.provider.config;

import com.ananops.provider.alipay.AlipayProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Bridge Wang
 * @version 1.0
 * @date 2020/4/15 19:20
 */
@Component
public class AlipayPropertiesListener implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event){
        AlipayProperties.loadProperties();
    }
}

