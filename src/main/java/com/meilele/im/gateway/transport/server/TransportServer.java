package com.meilele.im.gateway.transport.server;

/**
 * 网关传输服务器
 * <p>
 * 
 * 描述:
 * 1.使网关具备接入各种传输协议的能力，如：TCP,Websocket,HTTP
 * 2.更好的动态扩展传输协议
 * <p>
 * 
 * @author Rayliu40k
 * @version $Id: TransportServer.java, v 0.1 2017年3月10日 下午1:51:39 Rayliu40k Exp $
 */
public interface TransportServer {

    /**
     * 服务器启动
     */
    public void start();

}
