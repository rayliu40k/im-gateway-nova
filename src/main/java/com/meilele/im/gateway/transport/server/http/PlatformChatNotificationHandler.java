/**
 * meilele.com Inc.
 * Copyright (c) 2004-2017 All Rights Reserved.
 */
package com.meilele.im.gateway.transport.server.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.meilele.im.core.event.ChatMessageDeliverEvent;
import com.meilele.im.gateway.transport.server.http.handler.PlatformOutBound;

/**
 * http下行聊天消息 
 * 
 * @author fengbo1
 * @version $Id: HttpChatNotificationMessageHandler.java, v 0.1 2017年4月5日 上午10:30:17 fengbo1 Exp $
 */
@RabbitListener(queues = "q.im-gateway.share")
@Component
public class PlatformChatNotificationHandler {

    /** 日志 */
    private static final Logger logger = LoggerFactory
        .getLogger(PlatformChatNotificationHandler.class);

    @Autowired
    private PlatformOutBound    httpMessageHandler;

    /**
     * 获取到聊天消息下行
     * 
     * @param event {@link ChatMessageDeliverEvent}
     */
    @RabbitHandler
    protected void handle(ChatMessageDeliverEvent event) {
        logger.info("【http聊天通知】消息模型：{}", event);
        httpMessageHandler.out(event);

    }
}
