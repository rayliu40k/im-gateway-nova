package com.meilele.im.gateway.ndp.lifecycle;

/**
 * 阶段改变监听器
 * 
 * @author Rayliu40k
 * @version $Id: PhaseChangeListener.java, v 0.1 2017年3月14日 下午12:36:13 Rayliu40k Exp $
 */
public interface PhaseChangeListener {

    /**
     * 改变阶段
     * 
     * @param nextPhase 改变后的阶段
     */
    void phaseChange(String nextPhase);
}
