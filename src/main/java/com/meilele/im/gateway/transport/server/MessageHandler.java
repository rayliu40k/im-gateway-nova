package com.meilele.im.gateway.transport.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import com.meilele.im.gateway.ndp.lifecycle.AccessPointManager;
import com.meilele.im.gateway.ndp.lifecycle.NDPLifecycle;
import com.meilele.im.gateway.ndp.model.AccessPoint;
import com.meilele.im.gateway.ndp.model.NDPLifecycleExecutor;
import com.meilele.im.gateway.ndp.model.NDPPacket;

import lombok.Data;

/**
 * 消息处理器
 * 
 * @author Rayliu40k
 * @version $Id: MessageHandler.java, v 0.1 2017年3月21日 下午1:35:39 Rayliu40k Exp $
 */
public abstract class MessageHandler<T> {

    /** 日志 */
    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    /**
     * 处理消息
     * 
     * @param event 消息事件
     */
    protected void handle(T event) {
        MessageHandlerParam param = this.wrapEventToNDPPacket(event);
        this.handle(param.getAccessPointId(), param.getPacket());
    }

    /**
     * 包装Event模型到NDPPacket模型
     * 
     * @param event 消息模型
     * @return {@link NDPPacket}
     */
    abstract protected MessageHandlerParam wrapEventToNDPPacket(T event);

    /**
     * 发送NDP协议报文
     * 
     * @param packet {@link NDPPacket}
     */

    /**
     * 发送NDP协议报文
     * 
     * @param accessPointId 接入点id
     * @param packet {@link NDPPacket}
     */
    protected void handle(String accessPointId, NDPPacket packet) {
        AccessPoint accessPoint = AccessPointManager.findAccessPoint(accessPointId);
        if (ObjectUtils.isEmpty(accessPoint)) {
            logger.warn("【消息处理异常】找不到AccessPoint,accessPointId：{}", accessPointId);
            return;
        }
        NDPLifecycleExecutor executor = accessPoint.getExecutor();
        NDPLifecycle lifecycle = executor.retrieveNDPLifecycle();
        lifecycle.execute(executor, packet);
    }

    /**
     * 消息处理器
     * 
     * @author Rayliu40k
     * @version $Id: MessageHandlerParam.java, v 0.1 2017年3月21日 下午2:54:49 Rayliu40k Exp $
     */
    @Data
    public class MessageHandlerParam {
        /** 接入点id */
        private String    accessPointId;

        /** NDP报文对象 */
        private NDPPacket packet;
    }

}
