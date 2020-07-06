package com.ananops.provider.config;

import io.fabric8.kubernetes.client.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by rongshuai on 2020/6/26 20:51
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "kubernetes")
public class K8sConfig {
    private static final Logger logger = LoggerFactory.getLogger(K8sConfig.class);

    private String master;

    @Bean
    public KubernetesClient K8sClient() throws KubernetesClientException{
        Config config = new ConfigBuilder().withMasterUrl(master).build();
        return new DefaultKubernetesClient(config);
    }
}
