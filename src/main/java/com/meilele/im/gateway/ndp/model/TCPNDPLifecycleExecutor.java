package com.meilele.im.gateway.ndp.model;

import java.util.concurrent.TimeUnit;

import com.meilele.im.gateway.constants.Constants;
import com.meilele.im.gateway.ndp.lifecycle.NDPLifecycle;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * TCP协议服务器中的NDP生命周期执行器
 * 
 * @author Rayliu40k
 * @version $Id: TCPNDPLifecycleExecutor.java, v 0.1 2017年6月5日 下午5:03:16 Rayliu40k Exp $
 */
public class TCPNDPLifecycleExecutor extends DefaultNDPLifecycleExecutor<ChannelHandlerContext> {

    /**
     * 构造方法
     */
    public TCPNDPLifecycleExecutor(ChannelHandlerContext kernel) {
        super(kernel);
    }

    /**
     * @see com.meilele.im.gateway.ndp.model.NDPLifecycleExecutor#generateAccessPointId()
     */
    @Override
    public String generateAccessPointId() {
        return super.kernel.channel().id().asLongText();
    }

    /**
     * @see com.meilele.im.gateway.ndp.model.NDPLifecycleExecutor#retrieveNDPLifecycle()
     */
    @Override
    public NDPLifecycle retrieveNDPLifecycle() {
        return super.kernel.channel().attr(NDPLifecycle.NDP_LIFECYCLE).get();
    }

    /**
     * @see com.meilele.im.gateway.ndp.model.NDPLifecycleExecutor#writeNDPPacket(com.meilele.im.gateway.ndp.model.NDPPacket, java.lang.Object)
     */
    @Override
    public void writeNDPPacket(NDPPacket packet, ChannelFutureListener futureListener) {
        Channel channel = super.kernel.channel();
        if (channel.isOpen() && channel.isActive()) {
            channel.writeAndFlush(packet).addListener(futureListener);
        }
    }

    /**
     * @see com.meilele.im.gateway.ndp.model.DefaultNDPLifecycleExecutor#switchNDPKeepalive(long, long, long)
     */
    @Override
    public void switchNDPKeepalive(long readIdleTime, long writeIdleTime, long allIdleTime) {
        ChannelPipeline pipeline = this.kernel.pipeline();
        pipeline.remove(Constants.CHANNEL_IDLE_TIMER);
        pipeline.addAfter(Constants.LOGGING_HANDLER, Constants.NDP_IDLE_TIMER,
            new IdleStateHandler(readIdleTime, writeIdleTime, allIdleTime, TimeUnit.SECONDS));
    }

}
