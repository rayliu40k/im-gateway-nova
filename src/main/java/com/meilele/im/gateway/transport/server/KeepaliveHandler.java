package com.meilele.im.gateway.transport.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.meilele.common.util.UUIDGenerator;
import com.meilele.im.gateway.constants.Constants;
import com.meilele.im.gateway.ndp.model.NDPDefinition;
import com.meilele.im.gateway.ndp.model.NDPPacket;
import com.meilele.im.gateway.utils.ChannelUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

/**
 * 保活处理器
 * 
 * @author Rayliu40k
 * @version $Id: KeepaliveHandler.java, v 0.1 2017年3月23日 下午5:20:20 Rayliu40k Exp $
 */
@Component
@Scope("prototype")
public class KeepaliveHandler extends ChannelInboundHandlerAdapter {

    /** 日志 */
    private static final Logger                         logger            = LoggerFactory
        .getLogger(KeepaliveHandler.class);

    /** 保活上下文 */
    private static final AttributeKey<KeepaliveContext> KEEPALIVE_CONTEXT = AttributeKey
        .valueOf("keepaliveContext");

    /**
     * @see io.netty.channel.ChannelHandlerAdapter#channelActive(io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().attr(KEEPALIVE_CONTEXT).set(new KeepaliveContext());
        ctx.fireChannelActive();
    }

    /**
     * @see io.netty.channel.ChannelHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.channel().attr(KEEPALIVE_CONTEXT).get().cleanIdleDuration();
        logger.info("【NDP心跳计数器清零】收到报文：{}，Channel：{}", msg, ctx.channel());
        ctx.fireChannelRead(msg);
    }

    /**
     * @see io.netty.channel.ChannelHandlerAdapter#userEventTriggered(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            final IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                //Channel心跳计数器，[读idle]直接关闭连接
                if (this.isChannelIdleTimer(ctx)) {
                    logger.info("【Channel心跳计数器超时】关闭连接，Channel：{}", ctx.channel());
                    ctx.channel().close();
                } else {
                    //NDP心跳计数器，[读idle]计数or关闭连接
                    KeepaliveContext keepalive = ctx.channel().attr(KEEPALIVE_CONTEXT).get();
                    int number = keepalive.increaseIdleDuration();
                    logger.info("【触发NDP心跳计数】当前计数：{}/{}", number,
                        KeepaliveContext.MAX_IDLE_DURATION);
                    if (keepalive.isExceedIdleDuration()) {
                        logger.info("【NDP心跳计数器超时】关闭连接，Channel：{}", ctx.channel());
                        ctx.channel().close();
                    }
                }
            } else if (e.state() == IdleState.WRITER_IDLE) {
                //NDP心跳计数器，[写idle]发送ping
                if (!isChannelIdleTimer(ctx)) {
                    NDPPacket ping = new NDPPacket();
                    ping.setVersion(NDPDefinition.VERSION_NUMBER).setId(UUIDGenerator.getUUID())
                        .setType(NDPDefinition.Type.PING.getCode())
                        .setTerminal(NDPDefinition.Terminal.SERVER.getCode())
                        .setOs(NDPDefinition.Os.LINUX.getCode())
                        .setTransport(NDPDefinition.Transport.TCP.getCode())
                        .setPlatform(NDPDefinition.Platform.SERVER.getCode());
                    logger.info("【发送心跳ping】ping:{},channel:{}", ping, ctx.channel());
                    ChannelUtils.writeNDPPacket(ctx.channel(), ping,
                        new FireExceptionOnFailureFutureListener());
                }
            }
        }
    }

    /**
     * 是否为Channel心跳计数器
     * 
     * @param ctx {@link ChannelHandlerContext}
     */
    private boolean isChannelIdleTimer(ChannelHandlerContext ctx) {
        return !ObjectUtils.isEmpty(ctx.pipeline().get(Constants.CHANNEL_IDLE_TIMER));
    }
}
