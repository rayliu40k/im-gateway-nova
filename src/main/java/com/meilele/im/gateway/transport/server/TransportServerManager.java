package com.meilele.im.gateway.transport.server;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.Setter;

/**
 * 网关传输服务器管理者
 * 
 * @author Rayliu40k
 * @version $Id: TransportServerManager.java, v 0.1 2017年3月10日 下午1:47:47 Rayliu40k Exp $
 */
public class TransportServerManager {

    /** 日志 */
    private static final Logger   logger = LoggerFactory.getLogger(TransportServerManager.class);

    /** 传输服务器注册机 */
    private ExecutorService       register;

    /** 网关传输服务器列表 */
    @Autowired
    @Setter
    private List<TransportServer> transportServers;

    /**
     * 注册服务器
     */
    public void registerServers() {
        logger.info("【注册传输服务器】");
        //初始化校验
        if (CollectionUtils.isEmpty(transportServers)) {
            logger.error("【[ERR]传输服务器注册失败】未找到传输服务器");
            return;
        }
        //初始化注册机
        this.register = Executors.newFixedThreadPool(transportServers.size());

        //注册
        this.transportServers.forEach(server -> {
            this.register.submit(server::start);
        });
        
    }

}
