package com.meilele.im.gateway.transport.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meilele.im.gateway.enums.ErrorCode;
import com.meilele.im.gateway.exception.NDPException;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * NIO写失败异常触发监听器
 * <p>
 * 
 * 功能：IO操作失败打印日志并触发Channel异常
 * <p>
  * @author Rayliu40k
  * @version $Id: FireExceptionOnFailureFutureListener.java, v 0.1 2017年3月20日 上午11:44:12 Rayliu40k Exp $
  */
public class FireExceptionOnFailureFutureListener implements ChannelFutureListener {

    /** 日志 **/
    private static final Logger logger = LoggerFactory
        .getLogger(FireExceptionOnFailureFutureListener.class);

    /**
     * @see io.netty.util.concurrent.GenericFutureListener#operationComplete(io.netty.util.concurrent.Future)
     */
    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        //IO操作失败
        if (!future.isSuccess()) {
            logger.error("【发送NDPPacket失败】");
            //失败处理
            this.failureHandle();
            //触发异常到ChannelPipeline
            future.channel().pipeline()
                .fireExceptionCaught(new NDPException(ErrorCode.IO_ERROR, "I/O异常", future.cause()));
        }
    }

    /**
     * 失败处理
     */
    protected void failureHandle() {
        //默认实现为空
    }
}
