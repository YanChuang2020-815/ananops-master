package com.ananops.provider.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Created by rongshuai on 2020/2/17 16:55
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // This is not for websocket authorization, and this should most likely not be altered.
        http
                .csrf().disable()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers("/ws","/druid/**", "/pay/alipayCallback", "/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs", "/api/applications").permitAll()
                .anyRequest().permitAll().and().logout().permitAll();
    }
}
