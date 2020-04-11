package com.meilele.im.gateway.ndp.model;

import com.meilele.im.gateway.ndp.lifecycle.NDPLifecycle;

import io.netty.channel.ChannelFutureListener;

/**
 * NDP生命周期执行器
 * 
 * @author Rayliu40k
 * @version $Id: NDPLifecycleExecutor.java, v 0.1 2017年6月5日 下午6:01:05 Rayliu40k Exp $
 */
public interface NDPLifecycleExecutor {

    /**
     * 生成接入点id
     * 
     * @return 接入点id
     */
    String generateAccessPointId();

    /**
     * 取回NDP生命周期
     * 
     * @return {@link NDPLifecycle}
     */
    NDPLifecycle retrieveNDPLifecycle();

    /**
     * 写NDP协议报文
     * 
     * @param packet {@link NDPPacket}
     * @param futureListener {@link ChannelFutureListener}
     */
    void writeNDPPacket(NDPPacket packet, ChannelFutureListener futureListener);

    /**
     * 开启NDP协议保活计数器
     * 
     * @param readIdleTime read时时间
     * @param writeIdleTime write超时时间
     * @param allIdleTime all超时时间
     */
    void switchNDPKeepalive(long readIdleTime, long writeIdleTime, long allIdleTime);
}
