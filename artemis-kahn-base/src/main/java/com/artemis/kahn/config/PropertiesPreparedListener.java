package com.artemis.kahn.config;

import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

public class PropertiesPreparedListener implements ApplicationListener<ApplicationPreparedEvent> {


    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        //初始化一下参数配置
        PropertiesBean.initialize(context.getEnvironment());
    }
}
