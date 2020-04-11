package com.meilele.im.gateway.transport.server;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.meilele.im.gateway.ndp.lifecycle.AccessPointManager;
import com.meilele.im.gateway.ndp.lifecycle.NDPPhase;
import com.meilele.im.gateway.ndp.model.AccessPoint;

/**
 * 网关在线用户统计定时任务
 * <p>
 * 
 * 功能：
 * 1、统计该网关当前在线用户数
 * 2、打印用户详细信息
 * <p>
 * 
 * @author Rayliu40k
 * @version $Id: OnlineUserStatisticsScheduledTask.java, v 0.1 2016年12月30日 下午9:15:34 Rayliu40k Exp $
 */
@Component
public class OnlineUserStatisticsScheduledTask implements Runnable {

    /** 日志 */
    private static final Logger logger = LoggerFactory
        .getLogger(OnlineUserStatisticsScheduledTask.class);

    /** 网关名称 */
    @Value("#{'${gateway.name}'}")
    private String              gatewayName;

    /**
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            Collection<AccessPoint> accessPoints = AccessPointManager.findAll();
            if (CollectionUtils.isNotEmpty(accessPoints)) {
                //统计该网关当前在线用户数
                logger.info("【统计网关在线用户】网关名称:{},当前在线用户数:{}", this.gatewayName, accessPoints.size());
                //打印用户详细信息
                accessPoints.forEach(accessPoint -> {
                    NDPPhase phase = accessPoint.getExecutor().retrieveNDPLifecycle().getCurPhase();
                    logger.info("【在线用户】{}，当前阶段：{}", accessPoint, phase);
                });
            }
        } catch (Exception e) {
            logger.error("【[ERR]统计在线用户任务失败】", e);
        }
    }

}
