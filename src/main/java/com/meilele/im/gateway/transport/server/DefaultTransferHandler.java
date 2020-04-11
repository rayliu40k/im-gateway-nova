package com.meilele.im.gateway.transport.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;

import com.google.common.collect.ImmutableMap;
import com.meilele.common.exception.SystemException;
import com.meilele.common.model.FacadeInvokeResult;
import com.meilele.im.core.facade.UserStatusFacade;
import com.meilele.im.core.facade.param.ChangeUserStatusFacadeParam;
import com.meilele.im.gateway.exception.NDPException;
import com.meilele.im.gateway.ndp.lifecycle.AccessPointManager;
import com.meilele.im.gateway.ndp.lifecycle.ConnectedPhase;
import com.meilele.im.gateway.ndp.lifecycle.InPhase;
import com.meilele.im.gateway.ndp.lifecycle.NDPLifecycle;
import com.meilele.im.gateway.ndp.lifecycle.NDPPhase;
import com.meilele.im.gateway.ndp.lifecycle.OutPhase;
import com.meilele.im.gateway.ndp.model.AccessPoint;
import com.meilele.im.gateway.ndp.model.NDPDefinition;
import com.meilele.im.gateway.utils.IMCoreModelFactory;
import com.meilele.im.gateway.utils.PrototypeBeanCreator;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 默认协议转换处理器
 * <p>
 * 
 * 描述：转换传输协议TCP、Polling、WebSocket到数据协议NDP
 * <p> 
 * @author Rayliu40k
 * @version $Id: DefaultTransferHandler.java, v 0.1 2017年6月6日 下午2:59:12 Rayliu40k Exp $
 */
public class DefaultTransferHandler<I> extends SimpleChannelInboundHandler<I> {

    /** 日志 */
    private static final Logger    logger = LoggerFactory.getLogger(DefaultTransferHandler.class);

    /** 多实例Bean生成器 */
    @Autowired
    protected PrototypeBeanCreator prototypeBeanCreator;

    /** 网关名称 */
    @Value("#{'${gateway.name}'}")
    protected String               gatewayName;

    /** 网关TCP消息订阅主题 */
    @Value("#{'tp.im-core.${gateway.name}.tcp.message'}")
    protected String               gatewayTCPMessageTopic;

    /** TCP会话超时时间，单位(秒) */
    @Value("#{'${tcp.session.expire.time}'}")
    protected long                 sessionExpireTime;

    /** 用户在线接口(im-core) */
    @Autowired
    protected UserStatusFacade     userStatusFacade;

    /**
     * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, I msg) throws Exception {
    }

    /**
     * 初始化NDPLifecycle
     * 
     * @return {@link NDPLifecycle}
     */
    protected NDPLifecycle initNDPLifecycle() {
        //初始化NDPLifecycle
        NDPLifecycle lifecycle = this.prototypeBeanCreator.createBean(NDPLifecycle.class);
        NDPPhase connectedPhase = this.prototypeBeanCreator.createBean(ConnectedPhase.class)
            .addPhaseChangeListener(lifecycle);
        //初始化阶段
        lifecycle.setCurPhase(connectedPhase);
        //初始化阶段集合
        lifecycle.setPhases(ImmutableMap.of(NDPPhase.CONNECTED, connectedPhase, NDPPhase.IN,
            this.prototypeBeanCreator.createBean(InPhase.class).addPhaseChangeListener(lifecycle),
            NDPPhase.OUT, this.prototypeBeanCreator.createBean(OutPhase.class)
                .addPhaseChangeListener(lifecycle)));
        return lifecycle;
    }

    /**
     * 最后的工作
     * 
     * @param sessionId 用户会话id
     */
    protected void finalWorks(String sessionId) {
        //通知im-core用户要下线了
        logger.info("【通知im-core用户下线】sessionId：{}", sessionId);
        AccessPoint accessPoint = AccessPointManager.findAccessPoint(sessionId);
        if (ObjectUtils.isEmpty(accessPoint)) {
            logger.error("【通知im-core用户下线异常】未找到accessPoint");
            return;
        }
        ChangeUserStatusFacadeParam userStatus = IMCoreModelFactory.createUserStatus(sessionId,
            accessPoint.getTerminal(), accessPoint.getPlatform(), accessPoint.getPlatformIdentity(),
            this.gatewayName, this.gatewayTCPMessageTopic, accessPoint.getUser(),
            accessPoint.getUserIdentity(), NDPDefinition.Status.OFFLINE.getCode(),
            NDPDefinition.Force.TRUE.getCode(), sessionExpireTime, accessPoint.getTransport(),
            accessPoint.getOs());

        FacadeInvokeResult result = this.userStatusFacade.changeStatus(userStatus);
        if (!result.isSuccess()) {
            logger.error("【通知im-core用户下线】系统异常");
            throw new SystemException(result.getErrorCode(), result.getErrorMessage());
        }
        //清除AccessPoint
        accessPoint = AccessPointManager.removeAccessPoint(sessionId);
        logger.info("【清除AccessPoint】{}", accessPoint);
    }

    /**
     * 异常处理
     * 
     * @param cause {@link Throwable}
     */
    protected void exceptionHandle(Throwable cause) {
        if (cause instanceof NDPException) {
            logger.error("NDP生命周期运行时异常", cause);
        } else {
            logger.error("未知运行时异常", cause);
        }
    }
}
