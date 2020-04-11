package com.meilele.im.gateway.ndp.lifecycle;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.meilele.im.gateway.enums.ErrorCode;
import com.meilele.im.gateway.exception.NDPException;
import com.meilele.im.gateway.ndp.enums.NDPEvent;
import com.meilele.im.gateway.ndp.model.NDPDefinition;
import com.meilele.im.gateway.ndp.model.NDPLifecycleContext;
import com.meilele.im.gateway.ndp.model.NDPLifecycleExecutor;
import com.meilele.im.gateway.ndp.model.NDPPacket;

import io.netty.util.AttributeKey;

/**
 * NDP生命周期
 * <p>
 * 
 * 描述：NDP协议生命周期依次为以下3个阶段：
 * connected -> in -> out -> closed（最终态，不可见）
 * <p>
 * @author Rayliu40k
 * @version $Id: NDPLifecycle.java, v 0.1 2017年3月10日 下午5:49:54 Rayliu40k Exp $
 */
@Component
@Scope("prototype")
public class NDPLifecycle implements PhaseChangeListener {

    /** 日志 */
    private static final Logger                    logger        = LoggerFactory
        .getLogger(NDPLifecycle.class);

    /** NDP生命周期Key */
    public static final AttributeKey<NDPLifecycle> NDP_LIFECYCLE = AttributeKey
        .valueOf("NDPLifecycle");

    /** 阶段集合 */
    private Map<String, NDPPhase>                  phases;

    /** 当前阶段 */
    private NDPPhase                               curPhase;

    /**
     * 执行生命周期
     * 
     * @param executor {@link NDPLifecycleExecutor}
     * @param packet
     */
    public void execute(NDPLifecycleExecutor executor, NDPPacket packet) {
        logger.info("【开始执行NDP生命周期】");
        NDPEvent event = this.buildEvent(packet);
        this.curPhase.react(new NDPLifecycleContext(event, packet, executor));
    }

    /**
     * 构建NDP事件
     * 
     * @param packet {@link NDPPacket}
     * @return {@link NDPEvent}
     */
    private NDPEvent buildEvent(NDPPacket packet) {
        logger.info("【构建NDP事件】Type：{}", packet.getType());

        NDPDefinition.Type type = NDPDefinition.Type
            .valueOf(StringUtils.upperCase(packet.getType()));

        String event = StringUtils.EMPTY;

        switch (type) {
            case PRESENCE:
                String status = packet.getBody().get(NDPDefinition.STATUS);
                event = "NDP_PRESENCE_" + StringUtils.upperCase(status);
                break;
            case CHAT:
                event = "NDP_CHAT";
                break;
            case CHAT_NOTIFICATION:
                event = "NDP_CHAT_NOTIFICATION";
                break;
            case ACK:
                event = "NDP_ACK";
                break;
            case SYSTEM_NOTIFICATION:
                event = "NDP_SYSTEM_NOTIFICATION";
                break;
            case TCP_CLOSE:
                event = "TCP_CLOSE";
                break;
            case PONG:
                event = "NDP_PONG";
                break;
            default:
                logger.warn("【构建NDP事件】非法事件，Type：{}", type.getCode());
                throw new NDPException(ErrorCode.INVALID_EVENT_ON_PHASE, "非法事件");
        }

        return NDPEvent.valueOf(event);
    }

    /**
     * @see com.meilele.im.gateway.ndp.lifecycle.PhaseChangeListener#phaseChange(java.lang.String)
     */
    @Override
    public void phaseChange(String nextPhase) {
        this.curPhase = phases.get(nextPhase);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "NDP lifecycle on [" + this.curPhase.phaseName() + "]";
    }

    /**
     * Setter method for property <tt>phases</tt>.
     * 
     * @param phases value to be assigned to property phases
     */
    public void setPhases(Map<String, NDPPhase> phases) {
        this.phases = phases;
    }

    /**
     * Setter method for property <tt>curPhase</tt>.
     * 
     * @param curPhase value to be assigned to property curPhase
     */
    public void setCurPhase(NDPPhase curPhase) {
        this.curPhase = curPhase;
    }

    /**
     * Getter method for property <tt>curPhase</tt>.
     * 
     * @return property value of curPhase
     */
    public NDPPhase getCurPhase() {
        return curPhase;
    }

}