/**
 * meilele.com Inc.
 * Copyright (c) 2004-2017 All Rights Reserved.
 */
package com.meilele.im.gateway.transport.server.http.handler;

/**
 * 平台消息上行
 * 
 * @author fengbo1
 * @version $Id: PlatformInBound.java, v 0.1 2017年4月5日 下午2:38:31 fengbo1 Exp $
 */
public interface PlatformInBound {

    /**
     * 上行消息
     * 
     * @param revStr 发送过来的json字符串
     */
    public void in(String revStr);
}
