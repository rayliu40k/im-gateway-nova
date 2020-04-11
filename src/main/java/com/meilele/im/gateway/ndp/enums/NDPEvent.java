package com.meilele.im.gateway.ndp.enums;

/**
 * NDP事件枚举
 * 
 * @author Rayliu40k
 * @version $Id: NDPEvent.java, v 0.1 2017年3月13日 上午11:45:09 Rayliu40k Exp $
 */
public enum NDPEvent {

                      /** 上线 */
                      NDP_PRESENCE_ONLINE("ndpPresenceOnline"),

                      /** 下线 */
                      NDP_PRESENCE_OFFLINE("ndpPresenceOffline"),

                      /** 不可用 */
                      NDP_PRESENCE_UNAVAILABLE("ndpPresenceUnavailable"),

                      /** 聊天 */
                      NDP_CHAT("ndpChat"),

                      /** 聊天通知 */
                      NDP_CHAT_NOTIFICATION("ndpChatNotification"),

                      /** 回执 */
                      NDP_ACK("ndpAck"),

                      /** 系统通知 */
                      NDP_SYSTEM_NOTIFICATION("ndpSystemNotification"),

                      /** 心跳请求 */
                      NDP_PING("ndpPing"),

                      /** 心跳响应 */
                      NDP_PONG("ndpPong"),

                      /** 建立TCP连接 */
                      TCP_ESTABLISH("tcpEstablish"),

                      /** 关闭TCP连接 */
                      TCP_CLOSE("tcpClose");

    /**
     * 构造方法
     */
    private NDPEvent(String code) {
        this.code = code;
    }

    /** 事件代码 */
    private String code;

    /**
     * Getter method for property <tt>code</tt>.
     * 
     * @return property value of code
     */
    public String getCode() {
        return code;
    }

    /**
     * Setter method for property <tt>code</tt>.
     * 
     * @param code value to be assigned to property code
     */
    public void setCode(String code) {
        this.code = code;
    }
}
