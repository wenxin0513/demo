package com.example.myexcel.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 * @author zhouhong
 * @version 1.0
 * @title: ApplicationUtil
 * @description: TODO
 * @date 2019/12/13 12:04
 */
@Component
public class ApplicationUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    /**
     * 获取 MessageSourceAccessor
     * @return
     */
    public static MessageSourceAccessor getMSA() {
        MessageSourceAccessor messageSourceAccessor = context
                .getBean(MessageSourceAccessor.class);
        return messageSourceAccessor;
    }
}
