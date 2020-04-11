/**
 * meilele.com Inc.
 * Copyright (c) 2004-2017 All Rights Reserved.
 */
package com.meilele.im.gateway.transport.server.http.handler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.google.gson.Gson;
import com.meilele.common.exception.SystemException;
import com.meilele.common.model.FacadeInvokeDataResult;
import com.meilele.common.mq.EventPublisher;
import com.meilele.im.core.event.ChatMessageDeliverEvent;
import com.meilele.im.dealer.AllocationFacade;
import com.meilele.im.dealer.result.AllocationResult;
import com.meilele.im.gateway.constants.Constants;
import com.meilele.im.gateway.ndp.model.NDPDefinition;
import com.meilele.im.gateway.transport.server.http.param.HttpMessage;
import com.meilele.im.gateway.utils.IMCoreModelFactory;
import com.meilele.token.model.Token;

/**
 * 接收到微信消息的handler
 * 
 * @author fengbo1
 * @version $Id: AbstractPlatformMessageHandler.java, v 0.1 2017年4月5日 下午3:46:10 fengbo1 Exp $
 */
public abstract class AbstractPlatformMessageHandler<T> implements PlatformInBound,
                                                    PlatformOutBound, InitializingBean {
    /** 日志 */
    private static final Logger                logger  = LoggerFactory
        .getLogger(AbstractPlatformMessageHandler.class);

    /** 平台发送消息接口 */
    protected static final Map<String, String> ADDRESS = new HashMap<String, String>();

    /** 获取分配的人员 */
    @Autowired
    protected AllocationFacade                 allocationFacade;

    /** 发布者接口(MQ) */
    @Autowired
    protected EventPublisher                   eventPublisher;

    @Value("#{'${channel.pushmessage.address}'}")
    private String                             address;

    /** 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("【初始化address】address : " + address);
        if (StringUtils.isBlank(address)) {
            throw new RuntimeException("配置的平台push地址错误");
        }
        String platforms[] = address.split(",");
        Arrays.asList(platforms).parallelStream().forEach(platform -> {
            String p[] = platform.split("=");
            ADDRESS.put(p[0], p[1]);
        });
        logger.info("【初始化address】完成,ADDRESS : " + ADDRESS);
    }

    /**
     * 接收到http消息
     * 
     * @param revStr json字符串
     */
    public void in(String revStr) {
        //1.转换为http消息对象
        HttpMessage message = new Gson().fromJson(revStr, HttpMessage.class);
        //2.获取TO
        AllocationResult toUser = retrieveToIfAbsent(message);

        //3.构建messageDeliverEvent并投递给im-core
        deliverEvent(message, toUser.getUserId(), toUser.getUserType());
    }

    /**
     * 获取消息目标
     * 
     * @param message {@link HttpMessage}
     * @return 消息目标
     */
    private AllocationResult retrieveToIfAbsent(HttpMessage message) {
        logger.info("【获取消息目标】");
        //分配to
        logger.info("【调用im-dealer获取TO】wechatUserId：{},userIdentity：{}", message.getWechatUserId(),
            message.getWechatChannelId());
        FacadeInvokeDataResult<AllocationResult> result = allocationFacade
            .allocateStaff(message.getWechatUserId(), message.getWechatChannelId());
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

    /**
     * 投递聊天事件给im-core
     * 
     * @param message 聊天消息
     * @param to 客服
     */
    private void deliverEvent(HttpMessage message, String to, String toUserType) {
        //投递聊天消息  
        ChatMessageDeliverEvent userChat = IMCoreModelFactory.createUserChat(message.getMsgId(),
            message.getWechatUserId(), to, toUserType, message.getType(), message.getBody(),
            NDPDefinition.UserIdentity.CUSTOMER.getCode(), null, null,
            NDPDefinition.Transport.HTTP.getCode(), NDPDefinition.Platform.WECHAT.getCode(),
            message.getWechatChannelId(), null, null);
        logger.info("【投递聊天消息】userChat：{}", userChat);
        eventPublisher.publish(Constants.MESSAGE_EVENT, userChat);
    }

    /**
     * 下行消息，发送给微信平台
     * 
     * @param event 消息
     */
    public void out(ChatMessageDeliverEvent event) {
        //1.获取token
        Token token = getToken();

        //2.封装发送消息参数
        T message = assemblePlatformMessage(event, token);

        //3. 发送消息
        sendToPlatform(message);

    }

    /**
     * 获取平台的token
     * 
     * @return 
     */
    protected abstract Token getToken();

    /**
     * 组装成想要返回给平台的对象
     * 
     * @param event
     */
    protected abstract T assemblePlatformMessage(ChatMessageDeliverEvent event, Token token);

    /**
     * 将消息发送给平台
     * 
     * @param message
     */
    protected abstract void sendToPlatform(T message);

}
