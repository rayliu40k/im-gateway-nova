package com.meilele.im.gateway.ndp.lifecycle;

import com.meilele.im.gateway.ndp.model.NDPLifecycleContext;

/**
 * NDP生命阶段
 * 
 * @author Rayliu40k
 * @version $Id: NDPPhase.java, v 0.1 2017年3月13日 上午10:42:21 Rayliu40k Exp $
 */
public interface NDPPhase {

    /** Connected阶段 */
    String CONNECTED = "CONNECTED";

    /** IN阶段 */
    String IN        = "IN";

    /** OUT阶段 */
    String OUT       = "OUT";

    /**
     * 响应方法
     * 
     * @param context {@link NDPLifecycleContext}
     */
    void react(NDPLifecycleContext context);

    /**
     * 阶段名称
     * 
     * @return 阶段名称
     */
    String phaseName();

}
