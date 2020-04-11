package com.meilele.im.gateway.ndp.lifecycle;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.meilele.common.exception.SystemException;
import com.meilele.common.model.FacadeInvokeResult;
import com.meilele.common.mq.EventPublisher;
import com.meilele.common.util.UUIDGenerator;
import com.meilele.im.core.event.SystemNotificationMessageDeliverEvent;
import com.meilele.im.core.facade.param.ChangeUserStatusFacadeParam;
import com.meilele.im.gateway.constants.Constants;
import com.meilele.im.gateway.enums.ErrorCode;
import com.meilele.im.gateway.ndp.enums.NDPEvent;
import com.meilele.im.gateway.ndp.model.AccessPoint;
import com.meilele.im.gateway.ndp.model.NDPDefinition;
import com.meilele.im.gateway.ndp.model.NDPLifecycleContext;
import com.meilele.im.gateway.ndp.model.NDPLifecycleExecutor;
import com.meilele.im.gateway.ndp.model.NDPPacket;
import com.meilele.im.gateway.transport.server.FireExceptionOnFailureFutureListener;
import com.meilele.im.gateway.utils.IMCoreModelFactory;

/**
 * Connected阶段
 * 
 * @author Rayliu40k
 * @version $Id: ConnectedPhase.java, v 0.1 2017年3月13日 下午1:36:58 Rayliu40k Exp $
 */
@Component
@Scope("prototype")
public class ConnectedPhase extends AbstractNDPPhase {

    /** 日志 */
    private static final Logger         logger         = LoggerFactory
        .getLogger(ConnectedPhase.class);

    /** 改变阶段通知 */
    private static final String         NOTIFICATION   = "Connected -> In";

    /** 当前阶段允许的事件 */
    private static final List<NDPEvent> ALLOWED_EVENTS = Lists
        .newArrayList(NDPEvent.NDP_PRESENCE_ONLINE);

    /** TCP服务器读空闲时间 */
    @Value("#{'${gateway.tcp.server.read.idle.time}'}")
    private int                         readIdleTime;

    /** TCP服务器写空闲时间 */
    @Value("#{'${gateway.tcp.server.write.idle.time}'}")
    private int                         writeIdleTime;

    /** TCP服务器读写空闲时间 */
    @Value("#{'${gateway.tcp.server.all.idle.time}'}")
    private int                         allIdleTime;

    /**
     * @see com.meilele.im.gateway.ndp.lifecycle.AbstractNDPPhase#getAllowedEventsOnPhase()
     */
    @Override
    protected List<NDPEvent> getAllowedEventsOnPhase() {
        return ALLOWED_EVENTS;
    }

    /**
     * @see com.meilele.im.gateway.ndp.lifecycle.AbstractNDPPhase#pushToNextPhase()
     */
    @Override
    protected String pushToNextPhase() {
        super.phaseChangeListener.phaseChange(NDPPhase.IN);
        return NOTIFICATION;
    }

    /**
     * @see com.meilele.im.gateway.ndp.lifecycle.NDPPhase#phaseName()
     */
    @Override
    public String phaseName() {
        return NDPPhase.CONNECTED;
    }

    /**
     * 获取EventPublisher实例
     * 
     * @return {@link EventPublisher}
     */
    private EventPublisher getEventPublisher() {
        return super.eventPublisher;
    }

    /**
     * NDP_PRESENCE_ONLINE事件操作
     * 
     * @param context {@link NDPLifecycleContext}
     * @return 是否推进到下一个生命阶段
     */
    public boolean ndpPresenceOnline(NDPLifecycleContext context) {
        logger.info("【处理[上线]事件】NDPLifecycleContext：{}", context);
        //通知上线
        String sessionId = this.notifyPresenceOnline(context);
        //创建接入点
        this.createAccessPoint(sessionId, context.getExecutor(), context.getPacket());
        //开启NDP协议保活计数器
        this.switchNDPKeepalive(context.getExecutor());

        //需要推进阶段
        return true;
    }

    /**
     * 通知上线（同步调用im-core）
     * 
     * @param context {@link NDPLifecycleContext}
     * @return sessionId 
     */
    private String notifyPresenceOnline(NDPLifecycleContext context) {
        logger.info("【通知im-core上线】");
        NDPLifecycleExecutor executor = context.getExecutor();
        NDPPacket revPacket = context.getPacket();
        String sessionId = executor.generateAccessPointId();
        ChangeUserStatusFacadeParam userStatus = IMCoreModelFactory.createUserStatus(sessionId,
            revPacket.getTerminal(), revPacket.getPlatform(),
            revPacket.getOptions().get(NDPDefinition.PLATFORM_IDENTITY), super.gatewayName,
            super.gatewayTCPMessageTopic, revPacket.getBody().get(NDPDefinition.FROM),
            revPacket.getBody().get(NDPDefinition.USER_IDENTITY),
            revPacket.getBody().get(NDPDefinition.STATUS),
            revPacket.getBody().get(NDPDefinition.FORCE), super.sessionExpireTime,
            revPacket.getTransport(), revPacket.getOs());

        FacadeInvokeResult result = this.userStatusFacade.changeStatus(userStatus);
        if (!result.isSuccess()) {
            if (ErrorCode.USER_ALREADY_KICK_OFF == ErrorCode
                .valueOf(StringUtils.upperCase(result.getErrorCode()))) {
                logger.info("【上线失败】已经被挤出登录,userStatus：{}", userStatus);
                //发送已经被挤出登录的系统通知
                this.sendAlreadyBeingKickedOffSystemNotification(executor, revPacket);
            } else {
                logger.error("【上线失败】系统异常");
                throw new SystemException(result.getErrorCode(), result.getErrorMessage());
            }

        }
        //发送消息回执
        super.sendACK2Client(context.getExecutor(), revPacket.getId(),
            NDPDefinition.AckType.PRESENCE.getCode());

        return sessionId;
    }

    /**
     * 发送已经被挤出登录的系统通知
     * 
     * @param executor {@link NDPLifecycleExecutor}
     * @param revPacket {@link NDPPacket}
     */
    private void sendAlreadyBeingKickedOffSystemNotification(NDPLifecycleExecutor executor,
                                                             NDPPacket revPacket) {
        NDPPacket kickedOffSystemNotification = new NDPPacket();

        //协议必选头部
        kickedOffSystemNotification.setVersion(NDPDefinition.VERSION_NUMBER)
            .setId(UUIDGenerator.getUUID())
            .setType(NDPDefinition.Type.SYSTEM_NOTIFICATION.getCode())
            .setTerminal(revPacket.getTerminal()).setOs(revPacket.getOs())
            .setTransport(revPacket.getTransport()).setPlatform(revPacket.getPlatform())
            //协议可选头部
            .setOptions(NDPDefinition.PLATFORM_IDENTITY,
                revPacket.getOptions().get(NDPDefinition.PLATFORM_IDENTITY))
            //协议有效负载
            .setBody(NDPDefinition.NOTIFY_TYPE,
                NDPDefinition.NotifyType.USER_OFFLINE_REQUEST.getCode())
            .setBody(NDPDefinition.ACTION, "CLOSE")
            .setBody(NDPDefinition.MESSAGE, ErrorCode.USER_ALREADY_KICK_OFF.getDesc());

        //发送NDPPacket
        executor.writeNDPPacket(kickedOffSystemNotification,
            new FireExceptionOnFailureFutureListener() {
                /**
                 * @see com.meilele.im.gateway.transport.server.FireExceptionOnFailureFutureListener#failureHandle()
                 */
                @Override
                protected void failureHandle() {
                    //通知im-core系统通知发送失败
                    SystemNotificationMessageDeliverEvent event = IMCoreModelFactory
                        .createMessageSystemNotification(UUIDGenerator.getUUID(),
                            SystemNotificationMessageDeliverEvent.Content.Type.MESSAGE_DELIVER_FAIL,
                            revPacket.getId(), revPacket.getType());
                    getEventPublisher().publish(Constants.MESSAGE_EVENT, event);
                }
            });

        logger.info("【上线失败】发送已经被挤出登录的系统通知,kickedOffSystemNotification：{}",
            kickedOffSystemNotification);
    }

    /**
     * 创建接入点
     * 
     * @param id 接入点标识
     * @param ctx {@link ChannelHandlerContext}
     * @param revPacket {@link NDPPacket}
     */

    /**
     * 创建接入点
     * 
     * @param id 接入点标识
     * @param executor {@link NDPLifecycleExecutor}
     * @param revPacket {@link NDPPacket}
     */
    private void createAccessPoint(String id, NDPLifecycleExecutor executor, NDPPacket revPacket) {
        AccessPoint accessPoint = AccessPointManager.createAccessPoint(id, executor,
            revPacket.getTerminal(), revPacket.getOs(), revPacket.getTransport(),
            revPacket.getPlatform(), revPacket.getOptions().get(NDPDefinition.PLATFORM_IDENTITY),
            revPacket.getBody().get(NDPDefinition.FROM),
            revPacket.getBody().get(NDPDefinition.USER_IDENTITY),
            revPacket.getBody().get(NDPDefinition.FORCE));
        logger.info("【创建接入点】{}", accessPoint);
    }

    /**
     * 开启NDP协议保活计数器
     * 
     * @param executor {@link NDPLifecycleExecutor}
     */
    private void switchNDPKeepalive(NDPLifecycleExecutor executor) {
        logger.info("【切换保活计数器】Channel心跳计数器 -> NDP心跳计数器");
        executor.switchNDPKeepalive(this.readIdleTime, this.writeIdleTime, this.allIdleTime);
    }
}
