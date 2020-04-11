package com.meilele.im.gateway.transport.server.tcp.handler;

import java.util.List;

import com.google.gson.Gson;
import com.meilele.im.gateway.ndp.model.NDPPacket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * NDP报文对象到Json报文编码器
 * 
 * @author Rayliu40k
 * @version $Id: NDPPacket2JsonPacketEncoder.java, v 0.1 2017年3月10日 下午2:18:33 Rayliu40k Exp $
 */
public class NDPPacket2JsonPacketEncoder extends MessageToMessageEncoder<NDPPacket> {

    /**
     * @see io.netty.handler.codec.MessageToMessageEncoder#encode(io.netty.channel.ChannelHandlerContext, java.lang.Object, java.util.List)
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, NDPPacket packet,
                          List<Object> out) throws Exception {
        out.add(new Gson().toJson(packet));
    }

}
