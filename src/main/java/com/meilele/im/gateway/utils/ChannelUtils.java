package com.meilele.im.gateway.utils;

import com.meilele.im.gateway.ndp.model.NDPPacket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

/**
 * 连接工具类
 * 
 * @author Rayliu40k
 * @version $Id: ChannelUtils.java, v 0.1 2017年3月16日 下午12:31:24 Rayliu40k Exp $
 */
public class ChannelUtils {

    /**
     * 连接是否可用
     * 
     * @param channel {@link Channel}
     * @return Channel是否可用
     */
    public static boolean isAvailable(Channel channel) {
        return channel.isOpen() && channel.isActive();
    }

    /**
     * 关闭连接
     * 
     * @param channel {@link Channel}
     */
    public static void close(Channel channel) {
        if (isAvailable(channel)) {
            channel.close();
        }
    }

    /**
     * 写NDP协议报文
     * 
     * @param channel {@link Channel}
     * @param packet {@link NDPPacket}
     */
    public static void writeNDPPacket(Channel channel, NDPPacket packet,
                                      ChannelFutureListener listener) {
        if (isAvailable(channel)) {
            channel.writeAndFlush(packet).addListener(listener);
        }
    }
}
