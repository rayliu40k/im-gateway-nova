package com.meilele.im.gateway.transport.server;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 保活上下文
 * 
 * @author Rayliu40k
 * @version $Id: KeepaliveContext.java, v 0.1 2017年3月23日 下午4:30:37 Rayliu40k Exp $
 */
public class KeepaliveContext {

    /** 最大闲置周期 */
    public static final int MAX_IDLE_DURATION = 6;

    /** 当前闲置周期  */
    private AtomicInteger   idleDuration      = new AtomicInteger();

    /**
     * 增加闲置周期
     * 
     * @return 当前闲置周期
     */
    public int increaseIdleDuration() {
        return this.idleDuration.incrementAndGet();
    }

    /**
     * 重置闲置周期
     */
    public void cleanIdleDuration() {
        idleDuration.set(0);
    }

    /**
     * 是否超过闲置周期
     * 
     * @return 是否超过
     */
    public boolean isExceedIdleDuration() {
        return this.idleDuration.intValue() >= MAX_IDLE_DURATION;
    }

}
