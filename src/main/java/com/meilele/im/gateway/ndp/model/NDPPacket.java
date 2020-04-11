package com.meilele.im.gateway.ndp.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * NDP报文对象
 * 
 * @author Rayliu40k
 * @version $Id: NDPPacket.java, v 0.1 2017年3月10日 下午2:16:10 Rayliu40k Exp $
 */
public class NDPPacket {

    /** 协议版本号 */
    private String              version;

    /** 协议包id */
    private String              id;

    /** 协议包类型 */
    private String              type;

    /** 物理终端 */
    private String              terminal;

    /** 操作系统 */
    private String              os;

    /** 传输协议 */
    private String              transport;

    /** 应用平台 */
    private String              platform;

    /** 协议可选头部 */
    private Map<String, String> options = new HashMap<>();

    /** 协议有效负载 */
    private Map<String, String> body    = new HashMap<>();

    /**
     * Getter method for property <tt>version</tt>.
     * 
     * @return property value of version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Setter method for property <tt>version</tt>.
     * 
     * @param version value to be assigned to property version
     */
    public NDPPacket setVersion(String version) {
        this.version = version;
        return this;
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
    public NDPPacket setId(String id) {
        this.id = id;
        return this;
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
    public NDPPacket setType(String type) {
        this.type = type;
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
    public NDPPacket setTerminal(String terminal) {
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
    public NDPPacket setOs(String os) {
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
    public NDPPacket setTransport(String transport) {
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
    public NDPPacket setPlatform(String platform) {
        this.platform = platform;
        return this;
    }

    /**
     * Getter method for property <tt>options</tt>.
     * 
     * @return property value of options
     */
    public Map<String, String> getOptions() {
        return options;
    }

    /**
     * 设置options值
     */
    public NDPPacket setOptions(String key, String value) {
        this.options.put(key, value);
        return this;
    }

    /**
     * Getter method for property <tt>body</tt>.
     * 
     * @return property value of body
     */
    public Map<String, String> getBody() {
        return body;
    }

    /**
     * 设置body值
     */
    public NDPPacket setBody(String key, String value) {
        this.body.put(key, value);
        return this;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
