package com.meilele.im.gateway.ndp.model;

import com.corundumstudio.socketio.SocketIOClient;
import com.meilele.im.gateway.constants.Constants;
import com.meilele.im.gateway.ndp.lifecycle.NDPLifecycle;

import io.netty.channel.ChannelFutureListener;

/**
 * Web协议服务器中的NDP生命周期执行器
 * 
 * @author Rayliu40k
 * @version $Id: WebNDPLifecycleExecutor.java, v 0.1 2017年6月5日 下午10:16:21 Rayliu40k Exp $
 */
public class WebNDPLifecycleExecutor extends DefaultNDPLifecycleExecutor<SocketIOClient> {

    /**
     * 构造方法
     */
    public WebNDPLifecycleExecutor(SocketIOClient kernel) {
        super(kernel);
    }

    /**
     * @see com.meilele.im.gateway.ndp.model.NDPLifecycleExecutor#generateAccessPointId()
     */
    @Override
    public String generateAccessPointId() {
        return super.kernel.getSessionId().toString();
    }

    /**
     * @see com.meilele.im.gateway.ndp.model.NDPLifecycleExecutor#retrieveNDPLifecycle()
     */
    @Override
    public NDPLifecycle retrieveNDPLifecycle() {
        return super.kernel.get(NDPLifecycle.NDP_LIFECYCLE.name());
    }

    /**
     * @see com.meilele.im.gateway.ndp.model.NDPLifecycleExecutor#writeNDPPacket(com.meilele.im.gateway.ndp.model.NDPPacket, java.lang.Object)
     */
    @Override
    public void writeNDPPacket(NDPPacket packet, ChannelFutureListener futureListener) {
        SocketIOClient client = super.kernel;
        if (client.isChannelOpen()) {
            client.sendEvent(Constants.NDP_PACKET_EVENT, packet).addListener(futureListener);
        }
    }

}
