package com.meilele.im.gateway.ndp.model;

import org.springframework.util.ObjectUtils;

import com.meilele.im.gateway.enums.ErrorCode;
import com.meilele.im.gateway.exception.NDPException;

/**
 * 默认NDP生命周期执行器
 * 
 * @author Rayliu40k
 * @version $Id: DefaultNDPLifecycleExecutor.java, v 0.1 2017年6月6日 上午11:46:26 Rayliu40k Exp $
 */
public abstract class DefaultNDPLifecycleExecutor<K> implements NDPLifecycleExecutor {

    /** 执行内核 */
    protected K kernel;

    /**
     * 构造方法
     */
    public DefaultNDPLifecycleExecutor(K kernel) {
        if (ObjectUtils.isEmpty(kernel)) {
            throw new NDPException(ErrorCode.OBJECT_IS_NULL, "kernel对象为空");
        }
        this.kernel = kernel;
    }

    /**
     * @see com.meilele.im.gateway.ndp.model.NDPLifecycleExecutor#switchNDPKeepalive(long, long, long)
     */
    @Override
    public void switchNDPKeepalive(long readerIdleTime, long writerIdleTime, long allIdleTime) {
    }
}
