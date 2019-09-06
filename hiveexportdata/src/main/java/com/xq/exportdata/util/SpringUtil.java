package com.xq.exportdata.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringUtil.applicationContext==null) {
            SpringUtil.applicationContext = applicationContext;
        }
    }

    public static ApplicationContext getContext(){
        return applicationContext;
    }

    public static <T> T getBean(Class<T> clsName) {
        return applicationContext.getBean(clsName);
    }

    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    public static <T> T getBean(String beanName,Class<T> clsName) {
        return applicationContext.getBean(beanName,clsName);
    }

    public static String getProValue(String key) {
        return applicationContext.getEnvironment().getProperty(key);
    }

    public static <T> T getValue(String key,Class<T> cls) {
        return applicationContext.getEnvironment().getProperty(key,cls);
    }
}
