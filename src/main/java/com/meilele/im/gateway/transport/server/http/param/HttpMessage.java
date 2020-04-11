/**
 * meilele.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.meilele.im.gateway.transport.server.http.param;

import lombok.Data;

/**
 * http 接收到微信的报文
 * 
 * 
 * @author fengbo1
 * @version $Id: HttpMessage.java, v 0.1 2016年8月31日 下午4:52:04 fengbo1 Exp $
 */
@Data
public class HttpMessage {

    /** 消息ID */
    private String msgId;

    /** 消息发送方 */
    private String wechatUserId;

    /** 消息接收方 */
    private String wechatChannelId;

    /** 消息接收者 */
    private String to;

    /** 消息体 */
    private String body;

    /**消息内容体，类型  1.text 2.image 3.video 4.voice*/
    private String type;

    /**消息接收到的时间*/
    private String dateTime;

    /** 用户身份 **/
    private String userIdentity;

}
