package com.ananops.provider.uflo2;

import com.bstek.uflo.env.EnvironmentProvider;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;

@Component
public class MyUfloEnvironmentProvider implements EnvironmentProvider {

    @Autowired
    private SessionFactory sessionFactory;

    @Resource(name = "ufloTransactionManager")
    private PlatformTransactionManager platformTransactionManager;

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public PlatformTransactionManager getPlatformTransactionManager() {
        return platformTransactionManager;
    }

    public void setPlatformTransactionManager(PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
    }

    @Override
    public String getLoginUser() {
        return null;
    }

    @Override
    public String getCategoryId() {
        return "anonymous";
    }

}