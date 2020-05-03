package com.ananops.provider.uflo2;

import com.bstek.uflo.console.UfloServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean buildUfloServlet(){
        return new ServletRegistrationBean(new UfloServlet(),"/uflo/*");
    }
}