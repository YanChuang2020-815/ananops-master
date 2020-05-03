package com.ananops.provider.uflo2;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class UfloConfig  {


    @Bean("localSessionFactoryBean")
    public LocalSessionFactoryBean localSessionFactoryBean(DataSource dataSource) throws
            PropertyVetoException, IOException {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setPackagesToScan("com.bstek.uflo.model*");
        Properties prop = new Properties();
        prop.put("hibernate.dialect","org.hibernate.dialect.MySQL5Dialect");
        prop.put("hibernate.show_sql",true);
        prop.put("hibernate.hbm2ddl.auto","update");
        prop.put("hibernate.jdbc.batch_size",100);
        sessionFactoryBean.setHibernateProperties(prop);

        return sessionFactoryBean;
    }


    @Bean("ufloTransactionManager")
    public HibernateTransactionManager ufloTransactionManager(SessionFactory sessionFactory){
        HibernateTransactionManager hi = new HibernateTransactionManager();
        hi.setSessionFactory(sessionFactory);
        return hi;
    }


}