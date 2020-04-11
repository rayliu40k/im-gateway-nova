package com.meilele.im.gateway.ndp.lifecycle;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.meilele.im.gateway.ndp.enums.NDPEvent;
import com.meilele.im.gateway.ndp.model.NDPLifecycleContext;

/**
 * Out阶段
 * 
 * @author Rayliu40k
 * @version $Id: OutPhase.java, v 0.1 2017年3月13日 下午1:47:25 Rayliu40k Exp $
 */
@Component
@Scope("prototype")
public class OutPhase extends AbstractNDPPhase {

    /** 日志 */
    private static final Logger         logger         = LoggerFactory.getLogger(OutPhase.class);

    /** 当前阶段允许的事件 */
    private static final List<NDPEvent> ALLOWED_EVENTS = Lists.newArrayList(NDPEvent.TCP_CLOSE,
        NDPEvent.NDP_PONG);

    /**
     * @see com.meilele.im.gateway.ndp.lifecycle.AbstractNDPPhase#getAllowedEventsOnPhase()
     */
    @Override
    protected List<NDPEvent> getAllowedEventsOnPhase() {
        return ALLOWED_EVENTS;
    }

    /**
     * @see com.meilele.im.gateway.ndp.lifecycle.NDPPhase#phaseName()
     */
    @Override
    public String phaseName() {
        return NDPPhase.OUT;
    }

    /**
     * TCP_CLOSE事件操作
     * 
     * @param context {@link NDPLifecycleContext}
     * @return 是否推进到下一个生命阶段
     */
    public boolean tcpClose(NDPLifecycleContext context) {
        logger.info("【处理[关闭连接]事件】正常关闭连接");
        return false;
    }

    /**
     * ndpPong事件操作
     * 
     * @param context {@link NDPLifecycleContext}
     * @return 推进到下一个生命阶段
     */
    public boolean ndpPong(NDPLifecycleContext context) {
        logger.info("【收到NDP心跳响应】{}", context.getPacket());
        return false;
    }

}
