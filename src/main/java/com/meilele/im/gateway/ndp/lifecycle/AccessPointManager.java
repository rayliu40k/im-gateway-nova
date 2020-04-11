package com.meilele.im.gateway.ndp.lifecycle;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.meilele.im.gateway.ndp.model.AccessPoint;
import com.meilele.im.gateway.ndp.model.NDPLifecycleExecutor;

/**
 * 接入点管理器
 * 
 * @author Rayliu40k
 * @version $Id: AccessPointManager.java, v 0.1 2017年3月17日 上午10:43:50 Rayliu40k Exp $
 */
public class AccessPointManager {

    /** 
     * AccessPoint持有者
     * 
     * 描述：
     * 1.Key为AccessPoint的id
     * 2.Value为AccessPoint实例
     * 
     * */
    private static final Map<String, AccessPoint> ACCESS_POINT_HOLDER = new ConcurrentHashMap<>();

    /**
     * 创建AccessPoint
     * 
     * @param id 接入点id
     * @param channel 接入点连接
     * @param terminal 物理终端
     * @param os 操作系统
     * @param transport 传输协议
     * @param platform 应用平台
     * @param platformIdentity 应用平台额外标识
     * @param user 用户标识
     * @param userIdentity 用户身份标识
     */

    /**
    * 创建AccessPoint
     * 
     * @param id 接入点id
     * @param executor NDP生命周期执行器
     * @param terminal 物理终端
     * @param os 操作系统
     * @param transport 传输协议
     * @param platform 应用平台
     * @param platformIdentity 应用平台额外标识
     * @param user 用户标识
     * @param userIdentity 用户身份标识
     * @param force 强登录标识
     * @return {@link AccessPoint}
     */
    public static AccessPoint createAccessPoint(String id, NDPLifecycleExecutor executor,
                                                String terminal, String os, String transport,
                                                String platform, String platformIdentity,
                                                String user, String userIdentity, String force) {
        ACCESS_POINT_HOLDER.put(id, new AccessPoint(id, executor, terminal, os, transport, platform,
            platformIdentity, user, userIdentity, force));
        return findAccessPoint(id);
    }

    /**
     * 删除AccessPoint
     * 
     * @param id 接入点id
     * @return {@link AccessPoint}
     */
    public static AccessPoint removeAccessPoint(String id) {
        return ACCESS_POINT_HOLDER.remove(id);
    }

    /**
     * 查找AccessPoint
     * 
     * @param id 接入点id
     * @return {@link AccessPoint}
     */
    public static AccessPoint findAccessPoint(String id) {
        return ACCESS_POINT_HOLDER.get(id);
    }

    /**
     * 获取所有AccessPoint
     * 
     * @return AccessPoint列表
     */
    public static Collection<AccessPoint> findAll() {
        return ACCESS_POINT_HOLDER.values();
    }
}
