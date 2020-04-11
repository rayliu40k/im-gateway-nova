package com.meilele.im.gateway.transport.server.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.meilele.im.gateway.constants.Constants;
import com.meilele.im.gateway.ndp.model.NDPPacket;
import com.meilele.im.gateway.transport.server.AbstractTransportServer;
import com.meilele.im.gateway.transport.server.web.handler.WebTransferHandler;
import com.meilele.im.gateway.utils.PrototypeBeanCreator;

/**
 * Web协议服务器(基于polling和websocket)
 * 
 * @author Rayliu40k
 * @version $Id: WebTransportServer.java, v 0.1 2017年6月5日 下午4:04:28 Rayliu40k Exp $
 */
@Component
public class WebTransportServer extends AbstractTransportServer {

    /** 日志 */
    private static final Logger    logger = LoggerFactory.getLogger(WebTransportServer.class);

    /** 多实例Bean生成器 */
    @Autowired
    protected PrototypeBeanCreator prototypeBeanCreator;

    /** 网关地址 */
    @Value("#{'${gateway.host}'}")
    protected String               address;

    /** Web协议服务器监听端口 */
    @Value("#{'${gateway.web.server.port}'}")
    private int                    port;

    /** Web协议服务器接收最大帧长度 */
    @Value("#{'${gateway.web.server.max.frame.length}'}")
    private int                    maxFrameLength;

    /** Web协议服务器接收最大http内容长度 */
    @Value("#{'${gateway.web.server.max.http.content.length}'}")
    private int                    httpContentLength;

    /** 心跳间隔时间 */
    @Value("#{'${gateway.web.server.ping.interval}'}")
    private int                    pingInterval;

    /** 心跳超时时间 */
    @Value("#{'${gateway.web.server.ping.timeout}'}")
    private int                    pingTimeout;

    /**
     * @see com.meilele.im.gateway.transport.server.AbstractTransportServer#start()
     */
    @Override
    public void start() {
        //初始化配置
        Configuration config = new Configuration();
        config.setHostname(this.address);
        config.setPort(this.port);
        config.setMaxFramePayloadLength(this.maxFrameLength);
        config.setMaxHttpContentLength(this.httpContentLength);
        config.setPingInterval(this.pingInterval);
        config.setPingTimeout(this.pingTimeout);
        //注册Handler
        config.setExceptionListener(this.prototypeBeanCreator.createBean(WebTransferHandler.class));
        SocketIOServer server = new SocketIOServer(config);
        server.addConnectListener(this.prototypeBeanCreator.createBean(WebTransferHandler.class));
        server
            .addDisconnectListener(this.prototypeBeanCreator.createBean(WebTransferHandler.class));
        server.addEventListener(Constants.NDP_PACKET_EVENT, NDPPacket.class,
            this.prototypeBeanCreator.createBean(WebTransferHandler.class));
        //启动服务器
        try {
            server.start();
            logger.info("【启动传输服务器】服务器:{},监听端口:{}", this.getClass().getSimpleName(), this.port);
        } catch (Exception e) {
            logger.error("【[ERR]传输服务器发生异常】服务器:" + this.getClass().getSimpleName(), e);
        }

    }

}
