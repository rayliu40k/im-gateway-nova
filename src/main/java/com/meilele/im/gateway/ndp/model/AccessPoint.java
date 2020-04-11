package com.meilele.im.gateway.ndp.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 接入点
 * 
 * @author Rayliu40k
 * @version $Id: AccessPoint.java, v 0.1 2017年3月13日 下午3:24:35 Rayliu40k Exp $
 */
public class AccessPoint {

    /** 接入点标识 */
    private String               id;

    /** NDP生命周期执行器 */
    private NDPLifecycleExecutor executor;

    /** 物理终端 */
    private String               terminal;

    /** 操作系统 */
    private String               os;

    /** 传输协议 */
    private String               transport;

    /** 应用平台 */
    private String               platform;

    /** 应用平台额外标识 */
    private String               platformIdentity;

    /** 用户标识 */
    private String               user;

    /** 用户身份标识 */
    private String               userIdentity;

    /** 强登录标识 */
    private String               force;

    /**
     * 构造方法
     */
    public AccessPoint(String id, NDPLifecycleExecutor executor, String terminal, String os,
                       String transport, String platform, String platformIdentity, String user,
                       String userIdentity, String force) {
        this.id = id;
        this.executor = executor;
        this.terminal = terminal;
        this.os = os;
        this.transport = transport;
        this.platform = platform;
        this.platformIdentity = platformIdentity;
        this.user = user;
        this.userIdentity = userIdentity;
        this.force = force;
    }

    /**
     * Getter method for property <tt>id</tt>.
     * 
     * @return property value of id
     */
    public String getId() {
        return id;
    }

    /**
     * Setter method for property <tt>id</tt>.
     * 
     * @param id value to be assigned to property id
     */
    public AccessPoint setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Getter method for property <tt>executor</tt>.
     * 
     * @return property value of executor
     */
    public NDPLifecycleExecutor getExecutor() {
        return executor;
    }

    /**
     * Setter method for property <tt>executor</tt>.
     * 
     * @param executor value to be assigned to property executor
     */
    public AccessPoint setExecutor(NDPLifecycleExecutor executor) {
        this.executor = executor;
        return this;
    }

    /**
     * Getter method for property <tt>terminal</tt>.
     * 
     * @return property value of terminal
     */
    public String getTerminal() {
        return terminal;
    }

    /**
     * Setter method for property <tt>terminal</tt>.
     * 
     * @param terminal value to be assigned to property terminal
     */
    public AccessPoint setTerminal(String terminal) {
        this.terminal = terminal;
        return this;
    }

    /**
     * Getter method for property <tt>os</tt>.
     * 
     * @return property value of os
     */
    public String getOs() {
        return os;
    }

    /**
     * Setter method for property <tt>os</tt>.
     * 
     * @param os value to be assigned to property os
     */
    public AccessPoint setOs(String os) {
        this.os = os;
        return this;
    }

    /**
     * Getter method for property <tt>transport</tt>.
     * 
     * @return property value of transport
     */
    public String getTransport() {
        return transport;
    }

    /**
     * Setter method for property <tt>transport</tt>.
     * 
     * @param transport value to be assigned to property transport
     */
    public AccessPoint setTransport(String transport) {
        this.transport = transport;
        return this;
    }

    /**
     * Getter method for property <tt>platform</tt>.
     * 
     * @return property value of platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * Setter method for property <tt>platform</tt>.
     * 
     * @param platform value to be assigned to property platform
     */
    public AccessPoint setPlatform(String platform) {
        this.platform = platform;
        return this;
    }

    /**
     * Getter method for property <tt>platformIdentity</tt>.
     * 
     * @return property value of platformIdentity
     */
    public String getPlatformIdentity() {
        return platformIdentity;
    }

    /**
     * Setter method for property <tt>platformIdentity</tt>.
     * 
     * @param platformIdentity value to be assigned to property platformIdentity
     */
    public AccessPoint setPlatformIdentity(String platformIdentity) {
        this.platformIdentity = platformIdentity;
        return this;
    }

    /**
     * Getter method for property <tt>user</tt>.
     * 
     * @return property value of user
     */
    public String getUser() {
        return user;
    }

    /**
     * Setter method for property <tt>user</tt>.
     * 
     * @param user value to be assigned to property user
     */
    public AccessPoint setUser(String user) {
        this.user = user;
        return this;
    }

    /**
     * Getter method for property <tt>userIdentity</tt>.
     * 
     * @return property value of userIdentity
     */
    public String getUserIdentity() {
        return userIdentity;
    }

    /**
     * Setter method for property <tt>userIdentity</tt>.
     * 
     * @param userIdentity value to be assigned to property userIdentity
     */
    public AccessPoint setUserIdentity(String userIdentity) {
        this.userIdentity = userIdentity;
        return this;
    }

    /**
     * Getter method for property <tt>force</tt>.
     * 
     * @return property value of force
     */
    public String getForce() {
        return force;
    }

    /**
     * Setter method for property <tt>force</tt>.
     * 
     * @param force value to be assigned to property force
     */
    public AccessPoint setForce(String force) {
        this.force = force;
        return this;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
