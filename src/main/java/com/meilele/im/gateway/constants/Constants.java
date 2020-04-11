package com.meilele.im.gateway.constants;

import io.netty.util.AttributeKey;

/**
 * 网关常量类
 * 
 * @author Rayliu40k
 * @version $Id: Constants.java, v 0.1 2017年4月23日 下午3:12:10 Rayliu40k Exp $
 */
public interface Constants {

    /** 
     * Channel心跳计数器 
     * <p>
     * 
     * 描述：
     * 连接级别保活计数器，连接建立后，如果在指定时间内没有[收到]任何NDP协议报文，则断开连接
     * <p>
     * */
    String               CHANNEL_IDLE_TIMER         = "channel_idle_timer";

    /** 
     * NDP心跳计数器 
     * <p>
     * 
     * 描述：
     * NDP协议保活计数器，用户上线以后，如果在指定时间内没有[收发]任何NDP协议报文，则断开连接
     * <p>
     * */
    String               NDP_IDLE_TIMER             = "ndp_idle_timer";

    /** 日志处理器 */
    String               LOGGING_HANDLER            = "logging_handler";

    /** 用户所属平台账号*/
    String               USER_PLATFORM_IDENTITY     = "userPlatformIdentity";

    /** 目标消息id */
    String               TARGET_MESSAGE_ID          = "targetMessageId";

    /** 目标消息类型 */
    String               TARGET_MESSAGE_TYPE        = "targetMessageType";

    /** 消息事件  */
    String               MESSAGE_EVENT              = "tp.im-gateway.chat.message";

    /** 目标用户平台 */
    String               TO_USER_PLATFORM           = "toUserPlatform";

    /** 目标用户平台标识 */
    String               TO_USER_PLAFTFROM_IDENTITY = "toUserPlatfromIdentity";

    /** 轨迹ID */
    String               TRACE_ID                   = "traceId";

    /** 连接关闭者 */
    AttributeKey<String> CHANNEL_CLOSER             = AttributeKey.valueOf("channelCloser");

    /** 客户端 */
    String               CLIENT                     = "client";

    /** NDP协议包事件 */
    String               NDP_PACKET_EVENT           = "ndp_packet_event";

    /** 服务器 */
    String               SERVER                     = "server";
}
