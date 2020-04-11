package com.meilele.im.gateway.ndp.model;

import com.meilele.im.gateway.ndp.enums.NDPEvent;

import lombok.Data;

/**
 * NDP生命周期上下文
 * 
 * @author Rayliu40k
 * @version $Id: NDPLifecycleContext.java, v 0.1 2017年6月6日 上午11:38:18 Rayliu40k Exp $
 */
@Data
public class NDPLifecycleContext {

    /** NDP事件 */
    private NDPEvent             event;

    /** NDP报文对象 */
    private NDPPacket            packet;

    /** NDP生命周期执行器 */
    private NDPLifecycleExecutor executor;

    /**
     * 构造方法
     */
    public NDPLifecycleContext(NDPEvent event, NDPPacket packet, NDPLifecycleExecutor executor) {
        this.event = event;
        this.packet = packet;
        this.executor = executor;
    }

}
