package com.meilele.im.gateway.transport.server.tcp.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.meilele.common.util.UUIDGenerator;
import com.meilele.im.gateway.constants.Constants;
import com.meilele.im.gateway.ndp.lifecycle.NDPLifecycle;
import com.meilele.im.gateway.ndp.model.NDPDefinition;
import com.meilele.im.gateway.ndp.model.NDPPacket;
import com.meilele.im.gateway.ndp.model.TCPNDPLifecycleExecutor;
import com.meilele.im.gateway.transport.server.DefaultTransferHandler;
import com.meilele.im.gateway.utils.ChannelUtils;

import io.netty.channel.ChannelHandlerContext;

/**
 * TCP协议转换处理器
 * 
 * @author Rayliu40k
 * @version $Id: TCPTransferHandler.java, v 0.1 2017年6月6日 下午1:32:01 Rayliu40k Exp $
 */
@Component
@Scope("prototype")
public class TCPTransferHandler extends DefaultTransferHandler<NDPPacket> {

    /** 日志 */
    private static final Logger logger = LoggerFactory.getLogger(TCPTransferHandler.class);

    /**
     * @see io.netty.channel.ChannelHandlerAdapter#channelActive(io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        MDC.put(Constants.TRACE_ID, UUIDGenerator.getUUID());
        NDPLifecycle lifecycle = super.initNDPLifecycle();
        ctx.channel().attr(NDPLifecycle.NDP_LIFECYCLE).set(lifecycle);
        logger.info("【建立连接】Connection：{}，Lifecycle：{}", ctx.channel(), lifecycle);
        MDC.remove(Constants.TRACE_ID);
    }

    /**
     * @see io.netty.channel.ChannelHandlerAdapter#channelInactive(io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        MDC.put(Constants.TRACE_ID, UUIDGenerator.getUUID());
        logger.info("【关闭连接】Connection：{}，Lifecycle：{}", ctx.channel(), this.getNDPLifecycle(ctx));
        NDPPacket packet = new NDPPacket();
        packet.setType(NDPDefinition.Type.TCP_CLOSE.getCode());
        this.executeNDPLifecycle(ctx, packet);
        MDC.remove(Constants.TRACE_ID);
    }

    /**
     * @see io.netty.channel.SimpleChannelInboundHandler#messageReceived(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NDPPacket packet) throws Exception {
        MDC.put(Constants.TRACE_ID, UUIDGenerator.getUUID());
        logger.info("【收到NDP协议包】Connection：{}，Lifecycle：{}", ctx.channel(),
            this.getNDPLifecycle(ctx));
        this.executeNDPLifecycle(ctx, packet);
        MDC.remove(Constants.TRACE_ID);
    }

    /**
     * @see io.netty.channel.ChannelHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        MDC.put(Constants.TRACE_ID, UUIDGenerator.getUUID());
        logger.error("【服务器异常】Connection：{}，Lifecycle：{}", ctx.channel(), this.getNDPLifecycle(ctx));
        //异常处理
        super.exceptionHandle(cause);
        //最后的操作
        super.finalWorks(ctx.channel().id().asLongText());
        //关闭连接
        if (ChannelUtils.isAvailable(ctx.channel())) {
            ctx.channel().close();
        }
        MDC.remove(Constants.TRACE_ID);
    }

    /**
     * 得到NDPLifecycle实例
     * 
     * @param ctx {@link ChannelHandlerContext}
     * @return NDPLifecycle实例
     */
    private NDPLifecycle getNDPLifecycle(ChannelHandlerContext ctx) {
        return ctx.channel().attr(NDPLifecycle.NDP_LIFECYCLE).get();
    }

    /**
     * 执行NDPLifecycle
     * 
     * @param ctx {@link ChannelHandlerContext}
     * @param packet {@link NDPPacket}
     */
    private void executeNDPLifecycle(ChannelHandlerContext ctx, NDPPacket packet) {
        this.getNDPLifecycle(ctx).execute(new TCPNDPLifecycleExecutor(ctx), packet);
    }

}
