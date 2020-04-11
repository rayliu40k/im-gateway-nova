package com.meilele.im.gateway.transport.server.tcp;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.meilele.im.gateway.constants.Constants;
import com.meilele.im.gateway.transport.server.AbstractTransportServer;
import com.meilele.im.gateway.transport.server.KeepaliveHandler;
import com.meilele.im.gateway.transport.server.TransportServerConfig;
import com.meilele.im.gateway.transport.server.tcp.handler.JsonPacket2NDPPacketDecoder;
import com.meilele.im.gateway.transport.server.tcp.handler.NDPPacket2JsonPacketEncoder;
import com.meilele.im.gateway.transport.server.tcp.handler.TCPTransferHandler;
import com.meilele.im.gateway.utils.PrototypeBeanCreator;

import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

/**
 * TCP协议服务器
 * 
 * @author Rayliu40k
 * @version $Id: TCPProtocolServer.java, v 0.1 2016年8月26日 下午2:11:35 Rayliu40k Exp $
 */
@Component
public class TCPTransportServer extends AbstractTransportServer {

    /** 网关地址 */
    @Value("#{'${gateway.host}'}")
    protected String             address;

    /** TCP协议服务器监听端口 */
    @Value("#{'${gateway.tcp.server.port}'}")
    private int                  port;

    /** TCP协议服务器接收最大帧长度 */
    @Value("#{'${gateway.tcp.server.max.frame.length}'}")
    private int                  maxFrameLength;

    /** 多实例Bean生成器 */
    @Autowired
    private PrototypeBeanCreator prototypeBeanCreator;

    /**
     * @see com.meilele.im.gateway.transport.server.AbstractTransportServer#addChannelHandlers(io.netty.channel.socket.SocketChannel)
     */
    @Override
    protected void addChannelHandlers(SocketChannel channel) {
        channel.pipeline()
            .addLast(Constants.CHANNEL_IDLE_TIMER, new IdleStateHandler(10, 0, 0, TimeUnit.SECONDS))
            .addLast(new LengthFieldBasedFrameDecoder(this.maxFrameLength, 0, 4, 0, 4))
            .addLast(new JsonObjectDecoder(this.maxFrameLength))
            .addLast(new StringDecoder(CharsetUtil.UTF_8))
            .addLast(new JsonPacket2NDPPacketDecoder())
            .addLast(this.prototypeBeanCreator.createBean(KeepaliveHandler.class))
            .addLast(this.prototypeBeanCreator.createBean(TCPTransferHandler.class))
            .addLast(new LengthFieldPrepender(4)).addLast(new StringEncoder())
            .addLast(new NDPPacket2JsonPacketEncoder());
    }

    /**
     * @see com.meilele.im.gateway.transport.server.AbstractTransportServer#getTransportServerConfig()
     */
    @Override
    protected TransportServerConfig getTransportServerConfig() {
        return new TransportServerConfig(TCPTransportServer.class.getSimpleName(), this.address,
            this.port);
    }

}
