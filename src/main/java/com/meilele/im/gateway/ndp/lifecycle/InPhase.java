package com.meilele.im.gateway.ndp.lifecycle;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.meilele.common.exception.SystemException;
import com.meilele.common.model.FacadeInvokeDataResult;
import com.meilele.common.model.FacadeInvokeResult;
import com.meilele.common.mq.EventPublisher;
import com.meilele.common.util.UUIDGenerator;
import com.meilele.im.core.event.ChatMessageDeliverEvent;
import com.meilele.im.core.event.SystemNotificationMessageDeliverEvent;
import com.meilele.im.core.facade.param.ChangeUserStatusFacadeParam;
import com.meilele.im.dealer.result.AllocationResult;
import com.meilele.im.gateway.constants.Constants;
import com.meilele.im.gateway.enums.ErrorCode;
import com.meilele.im.gateway.exception.NDPException;
import com.meilele.im.gateway.ndp.enums.NDPEvent;
import com.meilele.im.gateway.ndp.model.AccessPoint;
import com.meilele.im.gateway.ndp.model.NDPDefinition;
import com.meilele.im.gateway.ndp.model.NDPLifecycleContext;
import com.meilele.im.gateway.ndp.model.NDPLifecycleExecutor;
import com.meilele.im.gateway.ndp.model.NDPPacket;
import com.meilele.im.gateway.transport.server.FireExceptionOnFailureFutureListener;
import com.meilele.im.gateway.utils.IMCoreModelFactory;

/**
 * In阶段
 * 
 * @author Rayliu40k
 * @version $Id: InPhase.java, v 0.1 2017年3月13日 下午1:37:50 Rayliu40k Exp $
 */
@Component
@Scope("prototype")
public class InPhase extends AbstractNDPPhase {

    /** 日志 */
    private static final Logger         logger         = LoggerFactory.getLogger(InPhase.class);

    /** 改变阶段通知 */
    private static final String         NOTIFICATION   = "In -> Out";

    /** 当前阶段允许的事件 */
    private static final List<NDPEvent> ALLOWED_EVENTS = Lists.newArrayList(
        NDPEvent.NDP_PRESENCE_UNAVAILABLE, NDPEvent.NDP_CHAT, NDPEvent.NDP_CHAT_NOTIFICATION,
        NDPEvent.NDP_SYSTEM_NOTIFICATION, NDPEvent.NDP_ACK, NDPEvent.NDP_PRESENCE_OFFLINE,
        NDPEvent.NDP_PONG);

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
        super.phaseChangeListener.phaseChange(NDPPhase.OUT);
        return NOTIFICATION;
    }

    /**
     * @see com.meilele.im.gateway.ndp.lifecycle.NDPPhase#phaseName()
     */
    @Override
    public String phaseName() {
        return NDPPhase.IN;
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
     * NDP_PRESENCE_UNAVAILABLE事件操作
     * 
     * @param context {@link NDPLifecycleContext}
     * @return 推进到下一个生命阶段
     */
    public boolean ndpPresenceUnavailable(NDPLifecycleContext context) {
        logger.info("【处理[不可用]事件】NDPLifecycleContext：{}", context);
        //查找当前AccessPoint
        AccessPoint accessPoint = AccessPointManager
            .findAccessPoint(context.getExecutor().generateAccessPointId());
        Optional<AccessPoint> op = Optional.ofNullable(accessPoint);
        op.<NDPException> orElseThrow(() -> {
            logger.error("【通知不可用异常】AccessPoint不存在");
            throw new NDPException(ErrorCode.OBJECT_IS_NULL, "AccessPoint不存在");
        });

        NDPPacket revPacket = context.getPacket();
        ChangeUserStatusFacadeParam userStatus = IMCoreModelFactory.createUserStatus(
            accessPoint.getId(), revPacket.getTerminal(), revPacket.getPlatform(),
            revPacket.getOptions().get(NDPDefinition.PLATFORM_IDENTITY), super.gatewayName,
            super.gatewayTCPMessageTopic, revPacket.getBody().get(NDPDefinition.FROM),
            revPacket.getBody().get(NDPDefinition.USER_IDENTITY),
            revPacket.getBody().get(NDPDefinition.STATUS),
            revPacket.getBody().get(NDPDefinition.FORCE), super.sessionExpireTime,
            revPacket.getTransport(), revPacket.getOs());

        //通知不可用
        logger.info("【通知im-core不可用】");
        FacadeInvokeResult result = this.userStatusFacade.changeStatus(userStatus);
        if (!result.isSuccess()) {
            logger.error("【通知不可用异常】系统异常");
            throw new SystemException(result.getErrorCode(), result.getErrorMessage());
        }

        //不需要推进阶段
        return false;
    }

    /**
     * NDP_CHAT事件操作
     * 
     * @param context {@link NDPLifecycleContext}
     * @return 推进到下一个生命阶段
     */
    public boolean ndpChat(NDPLifecycleContext context) {
        logger.info("【处理[聊天]事件】NDPLifecycleContext：{}", context);

        //查找当前AccessPoint
        AccessPoint accessPoint = AccessPointManager
            .findAccessPoint(context.getExecutor().generateAccessPointId());
        Optional<AccessPoint> op = Optional.ofNullable(accessPoint);
        op.<NDPException> orElseThrow(() -> {
            logger.error("【投递聊天异常】AccessPoint不存在");
            throw new NDPException(ErrorCode.OBJECT_IS_NULL, "AccessPoint不存在");
        });

        NDPPacket revPacket = context.getPacket();
        //获取消息目标
        AllocationResult result = this.retrieveToIfAbsent(revPacket);

        //投递聊天消息  
        ChatMessageDeliverEvent userChat = IMCoreModelFactory.createUserChat(revPacket.getId(),
            revPacket.getBody().get(NDPDefinition.FROM), result == null ? null : result.getUserId(),
            result == null ? null : result.getUserType(),
            revPacket.getBody().get(NDPDefinition.CONTENT_TYPE),
            revPacket.getBody().get(NDPDefinition.CONTENT), accessPoint.getUserIdentity(),
            revPacket.getOs(), revPacket.getTerminal(), revPacket.getTransport(),
            revPacket.getPlatform(), revPacket.getOptions().get(NDPDefinition.PLATFORM_IDENTITY),
            revPacket.getBody().get(Constants.TO_USER_PLATFORM),
            revPacket.getBody().get(Constants.TO_USER_PLAFTFROM_IDENTITY));
        logger.info("【投递聊天消息】userChat：{}", userChat);
        super.eventPublisher.publish(Constants.MESSAGE_EVENT, userChat);

        //发送消息回执
        super.sendACK2Client(context.getExecutor(), revPacket.getId(),
            NDPDefinition.AckType.CHAT.getCode());

        //不需要推进阶段
        return false;
    }

    /**
     * 接收到通知  NDP_SYSTEM_NOTIFICATION
     * 
     * @param context {@link NDPLifecycleContext}
     * @return 推进到下一个生命阶段
     */
    public boolean ndpSystemNotification(NDPLifecycleContext context) {
        logger.info("【处理[系统通知]事件】NDPLifecycleContext：{}", context);
        //查找当前AccessPoint
        AccessPoint accessPoint = AccessPointManager
            .findAccessPoint(context.getExecutor().generateAccessPointId());
        Optional<AccessPoint> op = Optional.ofNullable(accessPoint);
        op.<NDPException> orElseThrow(() -> {
            logger.error("【投递系统通知异常】AccessPoint不存在");
            throw new NDPException(ErrorCode.OBJECT_IS_NULL, "AccessPoint不存在");
        });
        NDPPacket revPacket = context.getPacket();

        NDPDefinition.NotifyType notifyType = NDPDefinition.NotifyType
            .valueOf(revPacket.getBody().get(NDPDefinition.NOTIFY_TYPE));
        switch (notifyType) {
            case STAFF_ALLOCATE_REQUEST:
                //1.分配客服请求的通知
                processAllocateRequest(context.getExecutor(), revPacket);
                break;
            case USER_OFFLINE_REQUEST:
                //2.用户下线通知
                processUserOfflineRequest(context.getExecutor(), revPacket);
                break;
            case STAFF_ALLOCATE_FAIL:
                processAllocateFail(context.getExecutor(), revPacket);
                break;
            case STAFF_ALLOCATE_SUCCESS:
                //分配成功
                processAllocateSuccess(context.getExecutor(), revPacket);
                break;
            default:
                break;
        }

        //不需要推进阶段
        return false;
    }

    /**
     * ndpPong事件操作
     * 
     * @param context {@link NDPLifecycleContext}
     * @return 推进到下一个生命阶段
     */
    public boolean ndpPong(NDPLifecycleContext context) {
        logger.info("【收到NDP心跳响应】pong：{}", context.getPacket());
        NDPPacket revPacket = context.getPacket();
        String sessionId = context.getExecutor().generateAccessPointId();
        AccessPoint accessPoint = AccessPointManager.findAccessPoint(sessionId);

        ChangeUserStatusFacadeParam userStatus = IMCoreModelFactory.createUserStatus(sessionId,
            revPacket.getTerminal(), revPacket.getPlatform(),
            revPacket.getOptions().get(NDPDefinition.PLATFORM_IDENTITY), super.gatewayName,
            super.gatewayTCPMessageTopic, accessPoint.getUser(), accessPoint.getUserIdentity(),
            NDPDefinition.Status.ONLINE.getCode(), NDPDefinition.Force.FALSE.getCode(),
            super.sessionExpireTime, revPacket.getTransport(), revPacket.getOs());

        this.userStatusFacade.changeStatus(userStatus);
        return false;
    }

    /**
     * 处理分配失败的通知，直接下发
     * 
     * @param channel
     * @param revPacket
     */

    /**
     * 处理分配失败的通知，直接下发
     * 
     * @param executor {@link NDPLifecycleExecutor}
     * @param revPacket {@link NDPPacket}
     */
    private void processAllocateSuccess(NDPLifecycleExecutor executor, NDPPacket revPacket) {
        logger.info("【分配成功通知】NDPLifecycleContext：{}", revPacket);
        executor.writeNDPPacket(revPacket, new FireExceptionOnFailureFutureListener() {
            /**
             * @see com.meilele.im.gateway.transport.server.FireExceptionOnFailureFutureListener#failureHandle()
             */
            @Override
            protected void failureHandle() {
                //通知im-core聊天通知发送失败
                SystemNotificationMessageDeliverEvent event = IMCoreModelFactory
                    .createMessageSystemNotification(UUIDGenerator.getUUID(),
                        SystemNotificationMessageDeliverEvent.Content.Type.MESSAGE_DELIVER_FAIL,
                        revPacket.getId(), revPacket.getType());
                getEventPublisher().publish(Constants.MESSAGE_EVENT, event);
            }
        });
    }

    /**
     * 处理分配失败的通知，直接下发
     * 
     * @param executor {@link NDPLifecycleExecutor}
     * @param revPacket {@link NDPPacket}
     */
    private void processAllocateFail(NDPLifecycleExecutor executor, NDPPacket revPacket) {
        logger.info("【分配失败通知】NDPLifecycleContext：{}", revPacket);
        executor.writeNDPPacket(revPacket, new FireExceptionOnFailureFutureListener() {
            /**
             * @see com.meilele.im.gateway.transport.server.FireExceptionOnFailureFutureListener#failureHandle()
             */
            @Override
            protected void failureHandle() {
                //通知im-core聊天通知发送失败
                SystemNotificationMessageDeliverEvent event = IMCoreModelFactory
                    .createMessageSystemNotification(UUIDGenerator.getUUID(),
                        SystemNotificationMessageDeliverEvent.Content.Type.MESSAGE_DELIVER_FAIL,
                        revPacket.getId(), revPacket.getType());
                getEventPublisher().publish(Constants.MESSAGE_EVENT, event);
            }
        });
    }

    /**
     * 处理分配客服的请求通知
     * 
     * @param executor {@link NDPLifecycleExecutor}
     * @param revPacket {@link NDPPacket}
     */
    private void processAllocateRequest(NDPLifecycleExecutor executor, NDPPacket revPacket) {
        //获取消息目标
        AllocationResult result = this.retrieveToIfAbsent(revPacket);

        //查看是否分配到客服
        if (result == null || StringUtils.isBlank(result.getUserId())) {
            //分配失败
            //系统发送通知给【用户】，分配失败
            String message = "客服暂不在线,如有咨询请留言。";
            SystemNotificationMessageDeliverEvent event = IMCoreModelFactory
                .createAllocateSystemNotification(revPacket.getId(), null, null,
                    NDPDefinition.Os.LINUX.getCode(), NDPDefinition.Terminal.SERVER.getCode(),
                    NDPDefinition.Transport.TCP.getCode(), NDPDefinition.Platform.SERVER.getCode(),
                    null, revPacket.getBody().get(NDPDefinition.FROM),
                    NDPDefinition.UserIdentity.STAFF.getCode(),
                    SystemNotificationMessageDeliverEvent.Content.Type.STAFF_ALLOCATE_FAIL, message,
                    null, null, null);
            logger.info("【分配客服失败】暂时无客服在线,通知给im-core：{}", event);
            super.eventPublisher.publish(Constants.MESSAGE_EVENT, event);
        } else {
            //分配成功
            //1.发送通知给【用户】，分配成功
            String toUserMessage = "客服已接入," + result.getUserId() + "为您服务!";
            String userId = result.getUserId();
            SystemNotificationMessageDeliverEvent toUserEvent = IMCoreModelFactory
                .createAllocateSystemNotification(revPacket.getId(), null, null,
                    NDPDefinition.Os.LINUX.getCode(), NDPDefinition.Terminal.SERVER.getCode(),
                    NDPDefinition.Transport.TCP.getCode(), NDPDefinition.Platform.SERVER.getCode(),
                    null, revPacket.getBody().get(NDPDefinition.FROM),
                    NDPDefinition.UserIdentity.CUSTOMER.getCode(),
                    SystemNotificationMessageDeliverEvent.Content.Type.STAFF_ALLOCATE_SUCCESS,
                    toUserMessage, userId, NDPDefinition.Platform.DKF.getCode(), null);
            logger.info("【分配客服成功】通知给[用户],event：{}", toUserEvent);
            super.eventPublisher.publish(Constants.MESSAGE_EVENT, toUserEvent);
            //2.发送通知给【客服】，分配成功
            String toStaffMessage = "你将为用户服务";
            SystemNotificationMessageDeliverEvent toStaffEvent = IMCoreModelFactory
                .createAllocateSystemNotification(revPacket.getId(), null, null,
                    NDPDefinition.Os.LINUX.getCode(), NDPDefinition.Terminal.SERVER.getCode(),
                    NDPDefinition.Transport.TCP.getCode(), NDPDefinition.Platform.SERVER.getCode(),
                    null, result.getUserId(), NDPDefinition.UserIdentity.STAFF.getCode(),
                    SystemNotificationMessageDeliverEvent.Content.Type.STAFF_ALLOCATE_SUCCESS,
                    toStaffMessage, revPacket.getBody().get(NDPDefinition.FROM),
                    revPacket.getPlatform(),
                    revPacket.getOptions().get(NDPDefinition.PLATFORM_IDENTITY));
            logger.info("【分配客服成功】通知给[客服],event：{}", toStaffEvent);
            super.eventPublisher.publish(Constants.MESSAGE_EVENT, toStaffEvent);
        }
        //发送消息回执
        super.sendACK2Client(executor, revPacket.getId(),
            NDPDefinition.AckType.SYSTEM_NOTIFICATION.getCode());
    }

    /**
     * 处理用户被踢下线通知
     * 
     * @param executor {@link NDPLifecycleExecutor}
     * @param packet {@link NDPPacket}
     */
    private void processUserOfflineRequest(NDPLifecycleExecutor executor, NDPPacket packet) {
        logger.info("【通知用户下线通知】NDPLifecycleContext：{}", packet);
        executor.writeNDPPacket(packet, new FireExceptionOnFailureFutureListener() {
            /**
             * @see com.meilele.im.gateway.transport.server.FireExceptionOnFailureFutureListener#failureHandle()
             */
            @Override
            protected void failureHandle() {
                //通知im-core系统通知发送失败
                SystemNotificationMessageDeliverEvent event = IMCoreModelFactory
                    .createMessageSystemNotification(UUIDGenerator.getUUID(),
                        SystemNotificationMessageDeliverEvent.Content.Type.MESSAGE_DELIVER_FAIL,
                        packet.getId(), packet.getType());
                getEventPublisher().publish(Constants.MESSAGE_EVENT, event);
            }
        });
    }

    /**
     * NDP_CHAT_NOTIFICATION
     * 
     * @param context {@link NDPLifecycleContext}
     * @return 推进到下一个生命阶段
     */
    public boolean ndpChatNotification(NDPLifecycleContext context) {
        logger.info("【接收到聊天通知】NDPLifecycleContext：{}", context);
        NDPPacket packet = context.getPacket();
        context.getExecutor().writeNDPPacket(packet, new FireExceptionOnFailureFutureListener() {
            /**
             * @see com.meilele.im.gateway.transport.server.FireExceptionOnFailureFutureListener#failureHandle()
             */
            @Override
            protected void failureHandle() {
                //通知im-core聊天通知发送失败
                SystemNotificationMessageDeliverEvent event = IMCoreModelFactory
                    .createMessageSystemNotification(UUIDGenerator.getUUID(),
                        SystemNotificationMessageDeliverEvent.Content.Type.MESSAGE_DELIVER_FAIL,
                        packet.getId(), packet.getType());
                getEventPublisher().publish(Constants.MESSAGE_EVENT, event);
            }
        });
        return false;
    }

    /**
     * NDP_ACK
     * 
     * @param context {@link NDPLifecycleContext}
     * @return 推进到下一个生命阶段
     */
    public boolean ndpAck(NDPLifecycleContext context) {
        logger.info("【接收到用户ACK】NDPLifecycleContext：{}", context);
        NDPPacket packet = context.getPacket();
        String packetAckId = packet.getBody().get(NDPDefinition.ACK_ID);
        String packetAckType = packet.getBody().get(NDPDefinition.ACK_TYPE);
        //通知im-core 该消息已经收到了
        logger.info("【通知im-core消息已经收到】");
        SystemNotificationMessageDeliverEvent event = IMCoreModelFactory
            .createMessageSystemNotification(packet.getId(),
                SystemNotificationMessageDeliverEvent.Content.Type.MESSAGE_RECEIVED, packetAckId,
                packetAckType);
        super.eventPublisher.publish(Constants.MESSAGE_EVENT, event);
        return false;
    }

    /**
     * NDP_PRESENCE_OFFLINE
     * 
     * @param context {@link NDPLifecycleContext}
     * @return 推进到下一个生命阶段
     */
    public boolean ndpPresenceOffline(NDPLifecycleContext context) {
        logger.info("【接收到用户下线请求】NDPLifecycleContext：{}", context);
        NDPPacket revPacket = context.getPacket();
        //1.通知im-core 用户要下线了
        logger.info("【通知im-core用户下线】");
        String sessionId = context.getExecutor().generateAccessPointId();
        ChangeUserStatusFacadeParam userStatus = IMCoreModelFactory.createUserStatus(sessionId,
            revPacket.getTerminal(), revPacket.getPlatform(),
            revPacket.getOptions().get(NDPDefinition.PLATFORM_IDENTITY), super.gatewayName,
            super.gatewayTCPMessageTopic, revPacket.getBody().get(NDPDefinition.FROM),
            revPacket.getBody().get(NDPDefinition.USER_IDENTITY),
            revPacket.getBody().get(NDPDefinition.STATUS), NDPDefinition.Force.TRUE.getCode(),
            super.sessionExpireTime, revPacket.getTransport(), revPacket.getOs());

        FacadeInvokeResult result = this.userStatusFacade.changeStatus(userStatus);
        if (!result.isSuccess()) {
            logger.error("【通知用户下线异常】系统异常");
            throw new SystemException(result.getErrorCode(), result.getErrorMessage());
        }

        //清除AccessPoint
        AccessPoint accessPoint = AccessPointManager.removeAccessPoint(sessionId);
        logger.info("【清除AccessPoint】{}", accessPoint);
        return true;
    }

    /**
     * 获取消息目标
     * 
     * @param revPacket {@link NDPPacket}
     * @return 消息目标
     */
    private AllocationResult retrieveToIfAbsent(NDPPacket revPacket) {
        logger.info("【获取消息目标】");
        String to = revPacket.getBody().get(NDPDefinition.TO);
        //to存在，直接返回
        if (StringUtils.isNotEmpty(to)) {
            AllocationResult result = new AllocationResult();
            result.setUserId(to);
            result.setUserType(NDPDefinition.UserIdentity.CUSTOMER.getCode());
            return result;
        }
        //分配to
        logger.info("【调用im-dealer获取TO】userId：{},userIdentity：{}",
            revPacket.getBody().get(NDPDefinition.FROM),
            revPacket.getOptions().get(NDPDefinition.PLATFORM_IDENTITY));
        FacadeInvokeDataResult<AllocationResult> result = allocationFacade.allocateStaff(
            revPacket.getBody().get(NDPDefinition.FROM),
            revPacket.getOptions().get(NDPDefinition.PLATFORM_IDENTITY));
        if (!result.isSuccess()) {
            logger.error("【分配员工失败】系统异常");
            throw new SystemException(result.getErrorCode(), result.getErrorMessage());
        }
        AllocationResult allocationResult = result.getData();
        if (allocationResult == null) {
            logger.warn("【分配员工失败】to为空");
        }
        logger.info("【获取消息目标结果】{}", allocationResult);
        return allocationResult;
    }
}
