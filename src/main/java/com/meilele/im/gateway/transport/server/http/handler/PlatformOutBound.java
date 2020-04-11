/**
 * meilele.com Inc.
 * Copyright (c) 2004-2017 All Rights Reserved.
 */
package com.meilele.im.gateway.transport.server.http.handler;

import com.meilele.im.core.event.ChatMessageDeliverEvent;

/**
 * 
 * @author fengbo1
 * @version $Id: PlatformOutBound.java, v 0.1 2017年4月5日 下午2:40:18 fengbo1 Exp $
 */
public interface PlatformOutBound {

    /**
     * 发送平台的消息 
     * 
     * @param event 消息事件
     */
    public void out(ChatMessageDeliverEvent event);
}
