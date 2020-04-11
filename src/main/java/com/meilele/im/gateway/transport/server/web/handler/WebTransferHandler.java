package com.meilele.im.gateway.transport.server.web.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.corundumstudio.socketio.listener.ExceptionListener;
import com.meilele.common.util.UUIDGenerator;
import com.meilele.im.gateway.constants.Constants;
import com.meilele.im.gateway.ndp.lifecycle.NDPLifecycle;
import com.meilele.im.gateway.ndp.model.NDPDefinition;
import com.meilele.im.gateway.ndp.model.NDPPacket;
import com.meilele.im.gateway.ndp.model.WebNDPLifecycleExecutor;
import com.meilele.im.gateway.transport.server.DefaultTransferHandler;

/**
 * Web协议连接建立处理器
 * 
 * @author Rayliu40k
 * @version $Id: WebChannelActiveHandler.java, v 0.1 2017年6月6日 下午2:24:36 Rayliu40k Exp $
 */
@Component
@Scope("prototype")
public class WebTransferHandler extends DefaultTransferHandler<Void> implements ConnectListener,
                                DisconnectListener, ExceptionListener, DataListener<NDPPacket> {

    /** 日志 */
    private static final Logger logger = LoggerFactory.getLogger(WebTransferHandler.class);

    /**
     * @see com.corundumstudio.socketio.listener.ConnectListener#onConnect(com.corundumstudio.socketio.SocketIOClient)
     */
    @Override
    public void onConnect(SocketIOClient client) {
        MDC.put(Constants.TRACE_ID, UUIDGenerator.getUUID());
        NDPLifecycle lifecycle = super.initNDPLifecycle();
        client.set(NDPLifecycle.NDP_LIFECYCLE.name(), lifecycle);
        logger.info("【建立连接】Connection：{}，Lifecycle：{}", client.getRemoteAddress(), lifecycle);
        MDC.remove(Constants.TRACE_ID);
    }

    /**
     * @see com.corundumstudio.socketio.listener.DataListener#onData(com.corundumstudio.socketio.SocketIOClient, java.lang.Object, com.corundumstudio.socketio.AckRequest)
     */
    @Override
    public void onData(SocketIOClient client, NDPPacket packet,
                       AckRequest ackSender) throws Exception {
        MDC.put(Constants.TRACE_ID, UUIDGenerator.getUUID());
        NDPLifecycle lifecycle = client.get(NDPLifecycle.NDP_LIFECYCLE.name());
        logger.info("【收到NDP协议包】Connection：{}，Lifecycle：{}", client.getRemoteAddress(), lifecycle);
        lifecycle.execute(new WebNDPLifecycleExecutor(client), packet);
        MDC.remove(Constants.TRACE_ID);
    }

    /**
     * @see com.corundumstudio.socketio.listener.DisconnectListener#onDisconnect(com.corundumstudio.socketio.SocketIOClient)
     */
    @Override
    public void onDisconnect(SocketIOClient client) {
        MDC.put(Constants.TRACE_ID, UUIDGenerator.getUUID());
        NDPLifecycle lifecycle = client.get(NDPLifecycle.NDP_LIFECYCLE.name());
        logger.info("【关闭连接】Connection：{}，Lifecycle：{}", client.getRemoteAddress(), lifecycle);
        NDPPacket packet = new NDPPacket();
        packet.setType(NDPDefinition.Type.TCP_CLOSE.getCode());
        lifecycle.execute(new WebNDPLifecycleExecutor(client), packet);
        MDC.remove(Constants.TRACE_ID);
    }

    /**
     * @see com.corundumstudio.socketio.listener.ExceptionListener#onEventException(java.lang.Exception, java.util.List, com.corundumstudio.socketio.SocketIOClient)
     */
    @Override
    public void onEventException(Exception cause, List<Object> args, SocketIOClient client) {
        this.handle(cause, client);
    }

    /**
     * @see com.corundumstudio.socketio.listener.ExceptionListener#onDisconnectException(java.lang.Exception, com.corundumstudio.socketio.SocketIOClient)
     */
    @Override
    public void onDisconnectException(Exception cause, SocketIOClient client) {
        this.handle(cause, client);
    }

    /**
     * @see com.corundumstudio.socketio.listener.ExceptionListener#onConnectException(java.lang.Exception, com.corundumstudio.socketio.SocketIOClient)
     */
    @Override
    public void onConnectException(Exception cause, SocketIOClient client) {
        this.handle(cause, client);
    }

    /**
     * 统一异常处理
     * 
     * @param cause 错误原因
     * @param client {@link SocketIOClient}
     */
    private void handle(Exception cause, SocketIOClient client) {
        MDC.put(Constants.TRACE_ID, UUIDGenerator.getUUID());
        logger.error("【服务器异常】Connection：{}，Lifecycle：{}", client.getRemoteAddress(),
            client.get(NDPLifecycle.NDP_LIFECYCLE.name()));
        //异常处理
        super.exceptionHandle(cause);
        //最后的操作
        super.finalWorks(client.getSessionId().toString());
        //关闭连接
        if (client.isChannelOpen()) {
            client.disconnect();
        }
        MDC.remove(Constants.TRACE_ID);
    }

}
