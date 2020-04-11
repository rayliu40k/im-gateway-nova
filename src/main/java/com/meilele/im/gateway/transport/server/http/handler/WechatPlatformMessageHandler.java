/**
 * meilele.com Inc.
 * Copyright (c) 2004-2017 All Rights Reserved.
 */
package com.meilele.im.gateway.transport.server.http.handler;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.RetryContext;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.meilele.common.exception.BussinessException;
import com.meilele.common.retry.DefaultRetryCallbackWrapper;
import com.meilele.common.retry.MRetry;
import com.meilele.common.util.UUIDGenerator;
import com.meilele.im.core.event.ChatMessageDeliverEvent;
import com.meilele.im.core.event.SystemNotificationMessageDeliverEvent;
import com.meilele.im.gateway.constants.Constants;
import com.meilele.im.gateway.transport.server.http.param.WechatMessage;
import com.meilele.im.gateway.transport.server.http.result.HttpResult;
import com.meilele.im.gateway.utils.IMCoreModelFactory;
import com.meilele.token.HttpClientUtils;
import com.meilele.token.manager.WechatTokenManager;
import com.meilele.token.model.Token;

/**
 * 深圳微信平台
 * 
 * @author fengbo1
 * @version $Id: WechatPlatformMessageHandler.java, v 0.1 2017年4月5日 下午2:59:41 fengbo1 Exp $
 */
@Component
public class WechatPlatformMessageHandler extends AbstractPlatformMessageHandler<WechatMessage> {
    /** 日志 */
    private static final Logger logger = LoggerFactory
        .getLogger(WechatPlatformMessageHandler.class);

    /** token管理器 */
    @Autowired
    private WechatTokenManager  tokenManager;

    /**
     * 
     * @see com.meilele.im.gateway.transport.server.http.handler.AbstractPlatformMessageHandler#getToken()
     */
    @Override
    protected Token getToken() {
        return tokenManager.getToken();
    }

    /**
     * 
     * @see com.meilele.im.gateway.transport.server.http.handler.AbstractPlatformMessageHandler#assemblePlatformMessage(com.meilele.im.core.event.ChatMessageDeliverEvent, com.meilele.token.model.Token)
     */
    @Override
    protected WechatMessage assemblePlatformMessage(ChatMessageDeliverEvent event, Token token) {
        WechatMessage wechatMessage = new WechatMessage();
        wechatMessage.setMsgId(event.getMessageId());
        wechatMessage.setBody(event.getContent().getBody());
        wechatMessage.setDateTime(String.valueOf(event.getCreatedTime().getTime()));
        wechatMessage.setO2oToken(token.getToken());
        wechatMessage.setType(event.getContent().getType());
        wechatMessage.setWechatUserId(event.getToUser().getId());
        wechatMessage.setWechatChannelId(event.getToUser().getPlatformIdentity());
        return wechatMessage;
    }

    /**
     * 
     * @see com.meilele.im.gateway.transport.server.http.handler.AbstractPlatformMessageHandler#sendToPlatform(java.lang.Object)
     */
    @Override
    protected void sendToPlatform(WechatMessage wechatMessage) {
        logger.info("【发送给平台消息，发送消息:" + wechatMessage + "】");
        String sendMessageUrl = ADDRESS.get(wechatMessage.getWechatChannelId());
        if (StringUtils.isBlank(sendMessageUrl)) {
            throw new BussinessException(
                "没有为 " + wechatMessage.getWechatChannelId() + " 配置任何发送消息地址");
        }

        MRetry.execute(3, new DefaultRetryCallbackWrapper<Void>() {
            @Override
            protected Void doRetry(RetryContext context) {
                String json = HttpClientUtils.httpPost(sendMessageUrl, wechatMessage.toString());
                if (json == null) {
                    throw new RuntimeException();
                }
                logger.info("【返回的值：" + json + "】");
                HttpResult httpResult = new Gson().fromJson(json, HttpResult.class);
                logger.info("【转换为对象后：" + httpResult + "】");
                if (!httpResult.isSuccess()) {
                    //发送失败
                    logger.error("【发送微信消息失败】");
                    throw new RuntimeException();
                } else {
                    notifyIMCore(wechatMessage);
                }
                return null;
            }
        });
    }

    /**
     * 通知im-core消息已经发送成功，并且已经"收到回执"，因为发消息给微信是HTTP同步调用，
     * 故只要HTTP同步调用返回成功，即表示发送成功，且算为"收到回执"
     * 
     * @param packet {@link WechatMessage}
     */
    private void notifyIMCore(WechatMessage packet) {
        logger.info("【通知im-core消息已经收到】packet = " + packet);
        SystemNotificationMessageDeliverEvent event = IMCoreModelFactory
            .createMessageSystemNotification(UUIDGenerator.getUUID(),
                SystemNotificationMessageDeliverEvent.Content.Type.MESSAGE_RECEIVED,
                packet.getMsgId(), "chat_notification");
        super.eventPublisher.publish(Constants.MESSAGE_EVENT, event);
    }

}
