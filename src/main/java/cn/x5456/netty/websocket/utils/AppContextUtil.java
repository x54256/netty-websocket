package cn.x5456.netty.websocket.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class AppContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(AppContextUtil.applicationContext == null) {
            AppContextUtil.applicationContext = applicationContext;
        }
        log.info("获取applicationContext～");
    }

    public static <T>T getBean(Class<T> cls) throws BeansException {
        return applicationContext.getBean(cls);
    }

}
