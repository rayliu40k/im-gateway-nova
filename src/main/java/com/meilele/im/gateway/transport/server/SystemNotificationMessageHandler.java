package com.meilele.im.gateway.transport.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.meilele.im.core.event.SystemNotificationMessageDeliverEvent;
import com.meilele.im.gateway.ndp.lifecycle.AccessPointManager;
import com.meilele.im.gateway.ndp.model.AccessPoint;
import com.meilele.im.gateway.ndp.model.NDPDefinition;
import com.meilele.im.gateway.ndp.model.NDPPacket;

/**
 * 系统通知消息处理器
 * 
 * @author Rayliu40k
 * @version $Id: SystemNotificationMessageHandler.java, v 0.1 2017年3月21日 下午3:02:40 Rayliu40k Exp $
 */
@RabbitListener(queues = "q.im-gateway.001.standlone")
@Component
public class SystemNotificationMessageHandler extends
                                              MessageHandler<SystemNotificationMessageDeliverEvent> {
    /** 日志 */
    private static final Logger logger = LoggerFactory
        .getLogger(SystemNotificationMessageHandler.class);

    /**
     * @see com.meilele.im.gateway.transport.server.MessageHandler#handle(java.lang.Object)
     */
    @RabbitHandler 
    protected void handle(SystemNotificationMessageDeliverEvent event) {
        logger.info("【系统通知】消息模型：{}", event);
        super.handle(event);
    }

    /**
     * @see com.meilele.im.gateway.transport.server.MessageHandler#wrapEventToNDPPacket(java.lang.Object)
     */
    @Override
    protected MessageHandler<SystemNotificationMessageDeliverEvent>.MessageHandlerParam wrapEventToNDPPacket(SystemNotificationMessageDeliverEvent event) {
        logger.info("【接收im-core[通知]消息】");
        MessageHandler<SystemNotificationMessageDeliverEvent>.MessageHandlerParam param = new MessageHandlerParam();
        //1.获取到[接收用户]接入点
        String accessPointId = event.getToUser().getSessionId();

        AccessPoint accessPoint = AccessPointManager.findAccessPoint(accessPointId);
        if (ObjectUtils.isEmpty(accessPoint)) {
            logger.error("【通知消息处理异常】sessionId为空");
            throw new AmqpRejectAndDontRequeueException("【通知消息处理异常】sessionId为空");
        }

        param.setAccessPointId(accessPointId);

        //2.组装packet
        NDPPacket packet = new NDPPacket();
        //2.1 组装头
        packet.setId(event.getMessageId()).setOs(NDPDefinition.Os.LINUX.getCode())
            .setPlatform(NDPDefinition.Platform.SERVER.getCode())
            .setTerminal(NDPDefinition.Terminal.SERVER.getCode())
            .setTransport(NDPDefinition.Transport.TCP.getCode())
            .setType(NDPDefinition.Type.SYSTEM_NOTIFICATION.getCode())
            .setVersion(NDPDefinition.VERSION_NUMBER)

            //2.2 组装option,由于是系统通知，没有PLATFORM_IDENTITY
            //.setOptions(NDPDefinition.PLATFORM_IDENTITY, event.get)

            //2.3 组装body
            .setBody(NDPDefinition.NOTIFY_TYPE, event.getContent().getType().name())
            .setBody(NDPDefinition.TO, event.getToUser().getId())
            .setBody(NDPDefinition.MESSAGE, event.getContent().getBody().get(NDPDefinition.MESSAGE))
            .setBody(NDPDefinition.USER_ID, event.getContent().getBody().get(NDPDefinition.USER_ID))
            .setBody(NDPDefinition.USER_PLATFORM, event.getContent().getBody().get(NDPDefinition.USER_PLATFORM))
            .setBody(NDPDefinition.USER_PLATFORM_IDENTITY, event.getContent().getBody().get(NDPDefinition.PLATFORM_IDENTITY));
        param.setPacket(packet);
        return param;
    }

}
