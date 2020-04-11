package com.meilele.im.gateway.transport.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.meilele.im.core.event.ChatMessageDeliverEvent;
import com.meilele.im.gateway.ndp.lifecycle.AccessPointManager;
import com.meilele.im.gateway.ndp.model.AccessPoint;
import com.meilele.im.gateway.ndp.model.NDPDefinition;
import com.meilele.im.gateway.ndp.model.NDPPacket;

/**
 * 聊天消息处理器
 * 
 * @author Rayliu40k
 * @version $Id: IMCoreMessageHandler.java, v 0.1 2017年3月21日 下午1:16:37 Rayliu40k Exp $
 */
@RabbitListener(queues = "q.im-gateway.001.standlone")
@Component
public class ChatNotificationMessageHandler extends MessageHandler<ChatMessageDeliverEvent> {

    /** 日志 */
    private static final Logger logger = LoggerFactory
        .getLogger(ChatNotificationMessageHandler.class);

    /**
     * @see com.meilele.im.gateway.transport.server.MessageHandler#handle(java.lang.Object)
     */
    @RabbitHandler
    protected void handle(ChatMessageDeliverEvent event) {
        logger.info("【聊天通知】消息模型：{}", event);
        super.handle(event);
    }

    /**
     * @see com.meilele.im.gateway.transport.server.MessageHandler#wrapEventToNDPPacket(java.lang.Object)
     */
    @Override
    protected MessageHandler<ChatMessageDeliverEvent>.MessageHandlerParam wrapEventToNDPPacket(ChatMessageDeliverEvent event) {
        logger.info("【接收im-core聊天消息下行通知】event= {}", event);
        String accessPointId = event.getToUser().getSessionId();
        AccessPoint accessPoint = AccessPointManager.findAccessPoint(accessPointId);
        if (ObjectUtils.isEmpty(accessPoint)) {
            logger.error("【聊天消息处理异常】sessionId为空");
            throw new AmqpRejectAndDontRequeueException("【聊天消息处理异常】sessionId为空");
        }

        MessageHandler<ChatMessageDeliverEvent>.MessageHandlerParam param = new MessageHandlerParam();
        param.setAccessPointId(accessPointId);
        NDPPacket packet = new NDPPacket();
        //设置基本属性
        packet.setId(event.getMessageId()).setOs(accessPoint.getOs())
            .setPlatform(event.getFromUser().getPlatformType())
            .setTerminal(event.getFromUser().getClientTerminal())
            .setTransport(event.getFromUser().getClientTransport())
            .setType(NDPDefinition.Type.CHAT_NOTIFICATION.getCode())
            .setVersion(NDPDefinition.VERSION_NUMBER);

        //设置option
        packet.setOptions(NDPDefinition.PLATFORM_IDENTITY,
            event.getFromUser().getPlatformIdentity());

        //设置body
        packet.setBody(NDPDefinition.FROM, event.getFromUser().getId())
            .setBody(NDPDefinition.TO, event.getToUser().getId())
            .setBody(NDPDefinition.RECEIVE_TIME, String.valueOf(event.getCreatedTime().getTime()))
            .setBody(NDPDefinition.CONTENT_TYPE, event.getContent().getType())
            .setBody(NDPDefinition.CONTENT, event.getContent().getBody());

        param.setPacket(packet);
        logger.info("【转换im-core聊天下行通知】packet = {}", param);
        return param;
    }

}
