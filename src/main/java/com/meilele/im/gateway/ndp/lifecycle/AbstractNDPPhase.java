package com.meilele.im.gateway.ndp.lifecycle;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import com.meilele.common.mq.EventPublisher;
import com.meilele.common.util.UUIDGenerator;
import com.meilele.im.core.facade.UserStatusFacade;
import com.meilele.im.dealer.AllocationFacade;
import com.meilele.im.gateway.enums.ErrorCode;
import com.meilele.im.gateway.exception.NDPException;
import com.meilele.im.gateway.ndp.enums.NDPEvent;
import com.meilele.im.gateway.ndp.model.NDPDefinition;
import com.meilele.im.gateway.ndp.model.NDPLifecycleContext;
import com.meilele.im.gateway.ndp.model.NDPLifecycleExecutor;
import com.meilele.im.gateway.ndp.model.NDPPacket;
import com.meilele.im.gateway.transport.server.FireExceptionOnFailureFutureListener;

/**
 * 抽象NDP阶段
 * <p>
 * 
 * 描述：NDP阶段流程骨架：
 * matchEvent -> reactEvent -> pushToNextPhase -> notifyChange
 * 
 * 1.matchEvent（在当前生命阶段匹配合法的事件）
 * 2.reactEvent（执行合法事件）
 * 3.pushToNextPhase（推进生命阶段）
 * 4.notifyChange（通知阶段改变）
 * <p>
 * @author Rayliu40k
 * @version $Id: AbstractNDPPhase.java, v 0.1 2017年3月13日 上午10:45:49 Rayliu40k Exp $
 */
public abstract class AbstractNDPPhase extends Observable implements NDPPhase {

    /** 日志 **/
    private static final Logger   logger = LoggerFactory.getLogger(AbstractNDPPhase.class);

    /** 网关名称 */
    @Value("#{'${gateway.name}'}")
    protected String              gatewayName;

    /** 网关TCP消息订阅主题 */
    @Value("#{'tp.im-core.${gateway.name}.tcp.message'}")
    protected String              gatewayTCPMessageTopic;

    /** TCP会话超时时间，单位(秒) */
    @Value("#{'${tcp.session.expire.time}'}")
    protected long                sessionExpireTime;

    /** 阶段改变监听器 */
    protected PhaseChangeListener phaseChangeListener;

    /** 用户在线接口(im-core) */
    @Autowired
    protected UserStatusFacade    userStatusFacade;

    /** 发布者接口(MQ) */
    @Autowired
    protected EventPublisher      eventPublisher;

    /** 获取分配的人员 */
    @Autowired
    protected AllocationFacade    allocationFacade;

    /**
     * 响应方法
     * 
     * @see com.meilele.im.gateway.ndp.lifecycle.NDPPhase#react(com.meilele.im.gateway.ndp.model.NDPLifecycleContext)
     */
    @Override
    public void react(NDPLifecycleContext context) {
        logger.info("【开始执行NDP阶段】当前阶段：{}", this.phaseName());
        //在当前生命阶段匹配合法的事件
        if (!this.matchEvent(context.getEvent())) {
            logger.error("【匹配NDP事件异常】非法事件：{}, 当前阶段合法的事件类型：{}", context.getEvent(),
                this.getAllowedEventsOnPhase());
            throw new NDPException(ErrorCode.INVALID_EVENT_ON_PHASE, "匹配NDP事件异常");
        }
        //执行事件
        if (this.reactEvent(context)) {
            //推进到下一个生命阶段
            String notification = this.pushToNextPhase();
            logger.info("【推进到下一个生命阶段】{}", notification);
            //通知生命阶段改变
            this.notifyChange(notification);
        }

    }

    /**
     * 匹配NDP事件
     * 
     * @param event {@link NDPEvent}
     * @return 是否匹配 
     */
    private boolean matchEvent(NDPEvent event) {
        logger.info("【匹配NDP事件】当前事件：{}", event);
        return this.getAllowedEventsOnPhase().contains(event);
    }

    /**
     * 通知生命阶段改变
     * 
     * @param notification 改变通知
     */
    protected void notifyChange(String notification) {
        logger.info("【通知生命阶段改变】");
        setChanged();
        notifyObservers(notification);
    }

    /**
     * 添加阶段改变监听器
     * 
     * @param listener {@link PhaseChangeListener}
     * @return {@link NDPPhase}
     */
    public NDPPhase addPhaseChangeListener(PhaseChangeListener listener) {
        this.phaseChangeListener = listener;
        return this;
    }

    /**
     * 获取当前阶段允许的事件
     * 
     * @return 允许的事件列表
     */
    abstract protected List<NDPEvent> getAllowedEventsOnPhase();

    /**
     * 1.执行事件
     * 2.确定是否推进到下一个生命阶段
     * 
     * @param context {@link NDPLifecycleContext}
     * @return 是否推进到下一个生命阶段
     */
    private boolean reactEvent(NDPLifecycleContext context) {
        logger.info("【执行事件】当前事件：{}", context.getEvent());
        Method method = ReflectionUtils.findMethod(this.getClass(), context.getEvent().getCode(),
            NDPLifecycleContext.class);
        if (ObjectUtils.isEmpty(method)) {
            logger.error("【非法事件方法】eventCode：{}", context.getEvent().getCode());
            throw new NDPException(ErrorCode.INVALID_EVENT_ON_PHASE, "非法事件方法");
        }
        try {
            return (boolean) ReflectionUtils.invokeMethod(method, this, context);
        } catch (Exception e) {
            logger.error("【执行事件异常】eventCode：" + context.getEvent().getCode(), e);
            throw new NDPException(ErrorCode.EVENT_EXECUTION_ERROR, "执行事件异常");
        }
    }

    /**
     * 推进到下一个生命阶段
     * 
     * @return 阶段推进通知
     */
    protected String pushToNextPhase() {
        return "Already Ending Phase";
    }

    /**
     * 发送ACK消息
     * 
     * @param executor {@link NDPLifecycleExecutor}
     * @param ackId 回执消息id
     * @param ackType 回执消息类型
     */
    protected void sendACK2Client(NDPLifecycleExecutor executor, String ackId, String ackType) {
        NDPPacket ack = new NDPPacket();
        ack.setVersion(NDPDefinition.VERSION_NUMBER).setId(UUIDGenerator.getUUID())
            .setType(NDPDefinition.Type.ACK.getCode())
            .setTerminal(NDPDefinition.Terminal.SERVER.getCode())
            .setOs(NDPDefinition.Os.LINUX.getCode())
            .setTransport(NDPDefinition.Transport.TCP.getCode())
            .setPlatform(NDPDefinition.Platform.SERVER.getCode())
            .setBody(NDPDefinition.ACK_ID, ackId).setBody(NDPDefinition.ACK_TYPE, ackType)
            .setBody(NDPDefinition.RECEIVE_TIME, String.valueOf(System.currentTimeMillis()));
        executor.writeNDPPacket(ack, new FireExceptionOnFailureFutureListener());
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return phaseName();
    }

}
