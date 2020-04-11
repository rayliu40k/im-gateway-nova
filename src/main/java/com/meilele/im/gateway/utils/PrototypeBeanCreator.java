package com.meilele.im.gateway.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * 多实例Bean生成器
 * <p>
 * 
 * 描述：多实例Bean生成器每次让Spring容器生成新实例
 * <p>
 * @author Rayliu40k
 * @version $Id: PrototypeBeanCreator.java, v 0.1 2016年8月30日 下午9:07:50 Rayliu40k Exp $
 */
@Component
public class PrototypeBeanCreator {

    /** Spring容器上下文 */
    @Autowired
    private WebApplicationContext context;

    /**
     * 创建Bean
     * 
     * @param clazz Bean类型
     * @return Bean实例
     */
    public <T> T createBean(Class<T> clazz) {
        return (T) context.getBean(clazz);
    }
}
