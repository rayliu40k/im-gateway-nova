package com.meilele.im.gateway.transport.server;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 传输服务器配置
 * 
 * @author Rayliu40k
 * @version $Id: TransportServerConfig.java, v 0.1 2017年3月10日 下午1:47:17 Rayliu40k Exp $
 */
public class TransportServerConfig {

    /** 服务器名称 */
    private String serverName;

    /** 服务器地址 */
    private String address;

    /** 服务器端口 */
    private int    port;

    /**
     * 构造方法
     */
    public TransportServerConfig(String serverName, String address, int port) {
        this.serverName = serverName;
        this.address = address;
        this.port = port;
    }

    /**
     * Getter method for property <tt>serverName</tt>.
     * 
     * @return property value of serverName
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Setter method for property <tt>serverName</tt>.
     * 
     * @param serverName value to be assigned to property serverName
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * Getter method for property <tt>address</tt>.
     * 
     * @return property value of address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Setter method for property <tt>address</tt>.
     * 
     * @param address value to be assigned to property address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Getter method for property <tt>port</tt>.
     * 
     * @return property value of port
     */
    public int getPort() {
        return port;
    }

    /**
     * Setter method for property <tt>port</tt>.
     * 
     * @param port value to be assigned to property port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
