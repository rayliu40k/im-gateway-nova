package com.meilele.im.gateway.launcher;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.meilele.im.gateway.transport.server.TransportServerManager;

/**
 * 网关启动器
 * <p>
 * 
 * 功能：spring容器启动同时启动网关
 * <p>
 * 
 * @author Rayliu40k
 * @version $Id: GatewayLauncher.java, v 0.1 2016年8月25日 下午4:13:41 Rayliu40k Exp $
 */
@Component
public class GatewayLauncher {

    /** 日志 */
    private static final Logger    logger = LoggerFactory.getLogger(GatewayLauncher.class);

    /** 网关名称 */
    @Value("#{'${gateway.name}'}")
    private String                 gatewayName;

    /** 网关协议服务器管理者 */
    @Autowired
    private TransportServerManager transportServerManager;

    /**
     * 开启网关
     */
    @PostConstruct
    public void start() {
        logger.info("【IM网关启动】网关名称:{}", this.gatewayName);
        this.transportServerManager.registerServers();
    }

}
