package com.meilele.im.gateway.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.meilele.im.core.event.ChatMessageDeliverEvent;
import com.meilele.im.core.event.MessageDeliverEvent.ToUser;
import com.meilele.im.core.event.MessageDeliverEvent.User;
import com.meilele.im.core.event.SystemNotificationMessageDeliverEvent;
import com.meilele.im.core.event.SystemNotificationMessageDeliverEvent.Content;
import com.meilele.im.core.facade.param.ChangeUserStatusFacadeParam;
import com.meilele.im.gateway.constants.Constants;
import com.meilele.im.gateway.ndp.model.NDPDefinition;

/**
 * IM核心模型工厂
 * 
 * @author Rayliu40k
 * @version $Id: IMCoreModelFactory.java, v 0.1 2017年3月20日 下午5:15:30 Rayliu40k Exp $
 */
public class IMCoreModelFactory {

    /**
     * 创建用户状态对象
     * 
     * @param sessionId 接入点标识
     * @param terminal 物理终端
     * @param platform 应用平台
     * @param platformIdentity 应用平台额外标识
     * @param gatewayName 网关名称
     * @param mqAddress 网关TCP消息订阅主题
     * @param userId 用户标识
     * @param userIdentity 用户身份标识
     * @param status 在线状态
     * @param force 强登录标识
     * @param timeout tcp会话超时
     * @param transport 传输协议
     * @param os 操作系统
     * @return {@link ChangeUserStatusFacadeParam}
     */
    public static ChangeUserStatusFacadeParam createUserStatus(String sessionId, String terminal,
                                                               String platform,
                                                               String platformIdentity,
                                                               String gatewayName, String mqAddress,
                                                               String userId, String userType,
                                                               String status, String force,
                                                               Long timeout, String transport,
                                                               String os) {
        ChangeUserStatusFacadeParam userStatus = new ChangeUserStatusFacadeParam();
        userStatus.setSessionId(sessionId);
        userStatus.setClientTerminal(terminal);
        userStatus.setPlatformType(platform);
        userStatus.setPlatformIdentity(platformIdentity);
        userStatus.setGatewayId(gatewayName);
        userStatus.setMqAddress(mqAddress);
        userStatus.setUserId(userId);
        userStatus.setUserType(userType);
        userStatus.setStatus(status);
        userStatus.setForce(Boolean.valueOf(force));
        userStatus.setTimeout(timeout);
        userStatus.setClientTransport(transport);
        userStatus.setClientOs(os);
        return userStatus;
    }

    /**
     * 创建用户聊天事件
     * 
     * @param messageId 消息id
     * @param from 消息来源
     * @param to 消息目标
     * @param contentType 消息类型
     * @param body 消息内容
     * @return {@link ChatMessageDeliverEvent}
     */
    public static ChatMessageDeliverEvent createUserChat(String messageId, String from, String to,
                                                         String toUserType, String contentType,
                                                         String body, String fromUserType,
                                                         String os, String terminal,
                                                         String transport, String fromUserplatform,
                                                         String fromUserplatformIdentity,
                                                         String toUserPlatform,
                                                         String toUserPlatformIdentity) {
        ChatMessageDeliverEvent userChat = new ChatMessageDeliverEvent();
        userChat.setMessageId(messageId);
        userChat.setCreatedTime(new Date());

        User fromUser = new User();
        fromUser.setId(from);
        fromUser.setClientOs(os);
        fromUser.setClientTerminal(terminal);
        fromUser.setClientTransport(transport);
        fromUser.setPlatformIdentity(fromUserplatformIdentity);
        fromUser.setPlatformType(fromUserplatform);
        fromUser.setType(fromUserType);
        userChat.setFromUser(fromUser);
        if (StringUtils.isNotBlank(to)) {
            ToUser toUser = new ToUser();
            toUser.setId(to);
            toUser.setType(toUserType);
            toUser.setPlatformIdentity(toUserPlatformIdentity);
            toUser.setPlatformType(toUserPlatform);
            userChat.setToUser(toUser);

        }

        ChatMessageDeliverEvent.Content content = new ChatMessageDeliverEvent.Content();
        content.setBody(body);
        content.setType(contentType);
        userChat.setContent(content);
        return userChat;
    }

    /**
     * 创建分配客服失败通知
     * 
     * @param messageId 消息id
     * @param from 消息来源
     * @param fromUserType 消息来源类型
     * @param os 操作系统
     * @param terminal 物理终端
     * @param transport 传输协议
     * @param platform 应用平台
     * @param platformIdentity 应用平台额外标识
     * @param to 消息目标
     * @param toUserType 消息目标类型
     * @param notifyType 系统通知类型
     * @param message 消息
     * @param userId 用户标识
     * @param userPlatform 用户平台
     * @param userPlatformIdentity 用户平台标识
     * @return {@link SystemNotificationMessageDeliverEvent}
     */
    public static SystemNotificationMessageDeliverEvent createAllocateSystemNotification(String messageId,
                                                                                         String from,
                                                                                         String fromUserType,
                                                                                         String os,
                                                                                         String terminal,
                                                                                         String transport,
                                                                                         String platform,
                                                                                         String platformIdentity,
                                                                                         String to,
                                                                                         String toUserType,
                                                                                         SystemNotificationMessageDeliverEvent.Content.Type notifyType,
                                                                                         String message,
                                                                                         String userId,
                                                                                         String userPlatform,
                                                                                         String userPlatformIdentity) {
        SystemNotificationMessageDeliverEvent event = new SystemNotificationMessageDeliverEvent();
        event.setMessageId(messageId);
        event.setCreatedTime(new Date());
        //构建fromUser属性
        User fromUser = new User();
        fromUser.setId(from);
        fromUser.setClientOs(os);
        fromUser.setClientTerminal(terminal);
        fromUser.setClientTransport(transport);
        fromUser.setPlatformIdentity(platformIdentity);
        fromUser.setPlatformType(platform);
        fromUser.setType(fromUserType);
        event.setFromUser(fromUser);
        //构建toUser属性
        ToUser toUser = new ToUser();
        toUser.setId(to);
        toUser.setType(toUserType);
        event.setToUser(toUser);

        //构建通知body内容
        Map<String, String> body = new HashMap<>();
        body.put(NDPDefinition.TO, to);
        body.put(NDPDefinition.MESSAGE, message);
        body.put(NDPDefinition.USER_ID, userId);
        body.put(NDPDefinition.USER_PLATFORM, userPlatform);
        body.put(NDPDefinition.PLATFORM_IDENTITY, userPlatformIdentity);

        Content content = new Content(notifyType, body);
        event.setContent(content);
        return event;
    }

    /**
     * 创建消息通知
     * 
     * @param messageId 消息id
     * @param messageType 消息类型
     * @return {@link SystemNotificationMessageDeliverEvent}
     */

    /**
     * 创建消息事件通知
     * 
     * @param eventType 事件类型
     * @param messageId 消息id
     * @param messageType 消息类型
     * @return {@link SystemNotificationMessageDeliverEvent}
     */
    public static SystemNotificationMessageDeliverEvent createMessageSystemNotification(String id,
                                                                                        SystemNotificationMessageDeliverEvent.Content.Type eventType,
                                                                                        String messageId,
                                                                                        String messageType) {
        SystemNotificationMessageDeliverEvent event = new SystemNotificationMessageDeliverEvent();
        event.setMessageId(id);
        event.setCreatedTime(new Date());
        Map<String, String> body = new HashMap<>();
        body.put(Constants.TARGET_MESSAGE_ID, messageId);
        body.put(Constants.TARGET_MESSAGE_TYPE, messageType);
        Content content = new Content(eventType, body);
        event.setContent(content);
        return event;
    }
}
