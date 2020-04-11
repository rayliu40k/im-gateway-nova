/**
 * meilele.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.meilele.im.gateway.transport.server.http.param;

import com.google.gson.Gson;

/**
 * 发送给微信消息对象
 * 
 * @author fengbo1
 * @version $Id: WechatMessage.java, v 0.1 2016年8月31日 下午7:10:24 fengbo1 Exp $
 */
public class WechatMessage {

    /** 消息ID **/
    private String msgId;

    /** token */
    private String o2oToken;

    /** 微信公众号（账号） */
    private String wechatChannelId;

    /** 微信用户ID **/
    private String wechatUserId;

    /** 消息类型 **/
    private String type;

    /*** 内容 */
    private String body;

    /** 时间 **/
    private String dateTime;

    /**
     * Getter method for property <tt>msgId</tt>.
     * 
     * @return property value of msgId
     */
    public String getMsgId() {
        return msgId;
    }

    /**
     * Setter method for property <tt>msgId</tt>.
     * 
     * @param msgId value to be assigned to property msgId
     */
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    /**
     * Getter method for property <tt>o2oToken</tt>.
     * 
     * @return property value of o2oToken
     */
    public String getO2oToken() {
        return o2oToken;
    }

    /**
     * Setter method for property <tt>o2oToken</tt>.
     * 
     * @param o2oToken value to be assigned to property o2oToken
     */
    public void setO2oToken(String o2oToken) {
        this.o2oToken = o2oToken;
    }

    /**
     * Getter method for property <tt>wechatChannelId</tt>.
     * 
     * @return property value of wechatChannelId
     */
    public String getWechatChannelId() {
        return wechatChannelId;
    }

    /**
     * Setter method for property <tt>wechatChannelId</tt>.
     * 
     * @param wechatChannelId value to be assigned to property wechatChannelId
     */
    public void setWechatChannelId(String wechatChannelId) {
        this.wechatChannelId = wechatChannelId;
    }

    /**
     * Getter method for property <tt>wechatUserId</tt>.
     * 
     * @return property value of wechatUserId
     */
    public String getWechatUserId() {
        return wechatUserId;
    }

    /**
     * Setter method for property <tt>wechatUserId</tt>.
     * 
     * @param wechatUserId value to be assigned to property wechatUserId
     */
    public void setWechatUserId(String wechatUserId) {
        this.wechatUserId = wechatUserId;
    }

    /**
     * Getter method for property <tt>type</tt>.
     * 
     * @return property value of type
     */
    public String getType() {
        return type;
    }

    /**
     * Setter method for property <tt>type</tt>.
     * 
     * @param type value to be assigned to property type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter method for property <tt>body</tt>.
     * 
     * @return property value of body
     */
    public String getBody() {
        return body;
    }

    /**
     * Setter method for property <tt>body</tt>.
     * 
     * @param body value to be assigned to property body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Getter method for property <tt>dateTime</tt>.
     * 
     * @return property value of dateTime
     */
    public String getDateTime() {
        return dateTime;
    }

    /**
     * Setter method for property <tt>dateTime</tt>.
     * 
     * @param dateTime value to be assigned to property dateTime
     */
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
