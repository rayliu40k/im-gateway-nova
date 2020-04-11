package com.meilele.im.gateway.ndp.model;

/**
 * NDP协议报文定义
 * 
 * @author Rayliu40k
 * @version $Id: NDPDefinition.java, v 0.1 2017年3月17日 上午11:41:58 Rayliu40k Exp $
 */
public class NDPDefinition {

    /**
     * 协议常量
     * 
     ************************************************************************/

    /** 协议版本号 */
    public static final String VERSION                = "version";

    /** 协议包id */
    public static final String ID                     = "id";

    /** 协议包类型 */
    public static final String TYPE                   = "type";

    /** 物理终端 */
    public static final String TERMINAL               = "terminal";

    /** 操作系统 */
    public static final String OS                     = "os";

    /** 传输协议 */
    public static final String TRANSPORT              = "transport";

    /** 应用平台 */
    public static final String PLATFORM               = "platform";

    /** 应用平台额外标识 */
    public static final String PLATFORM_IDENTITY      = "platformIdentity";

    /** 消息来源 */
    public static final String FROM                   = "from";

    /** 消息目标 */
    public static final String TO                     = "to";

    /** 身份标识 */
    public static final String USER_IDENTITY          = "userIdentity";

    /** 强登录标识 */
    public static final String FORCE                  = "force";

    /** 在线状态 */
    public static final String STATUS                 = "status";

    /** 有效负载类型 */
    public static final String CONTENT_TYPE           = "contentType";

    /** 有效负载内容 */
    public static final String CONTENT                = "content";

    /** 报文接收时间 */
    public static final String RECEIVE_TIME           = "recivedTime";

    /** 系统通知类型 */
    public static final String NOTIFY_TYPE            = "notifyType";

    /** 回执消息id */
    public static final String ACK_ID                 = "ackId";

    /** 回执消息类型 */
    public static final String ACK_TYPE               = "ackType";

    /** 系统通知Action */
    public static final String ACTION                 = "action";

    /** 系统通知Message */
    public static final String MESSAGE                = "message";

    /** 用户ID */
    public static final String USER_ID                = "userId";

    /** 用户所属平台 */
    public static final String USER_PLATFORM          = "userPlatform";

    /** 用户所属平台账号*/
    public static final String USER_PLATFORM_IDENTITY = "userPlatformIdentity";

    /***********************************************************************/

    /**
     * 协议常量值
     *
     ***********************************************************************/

    /** 协议版本号 */
    public static final String VERSION_NUMBER         = "1.0";

    /***********************************************************************/

    /**
     * 协议枚举
     * 
     ***********************************************************************/

    /**
     * 协议包类型
     * 
     * @author Rayliu40k
     * @version $Id: Type.java, v 0.1 2017年3月17日 下午1:09:45 Rayliu40k Exp $
     */
    public enum Type {

                      /** 在线状态 */
                      PRESENCE("presence"),

                      /** 聊天 */
                      CHAT("chat"),

                      /** 聊天通知 */
                      CHAT_NOTIFICATION("chat_notification"),

                      /** 回执 */
                      ACK("ack"),

                      /** 系统通知 */
                      SYSTEM_NOTIFICATION("system_notification"),

                      /** 心跳请求 */
                      PING("ping"),

                      /** 心跳响应 */
                      PONG("pong"),

                      /** 连接关闭（内部类型） */
                      TCP_CLOSE("tcp_close");

        /**
         * 构造方法
         */
        private Type(String code) {
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

    /**
     * 物理终端
     * 
     * @author Rayliu40k
     * @version $Id: Terminal.java, v 0.1 2017年3月17日 下午1:10:59 Rayliu40k Exp $
     */
    public enum Terminal {

                          /** 个人电脑 */
                          PC("pc"),

                          /** 手机 */
                          MOBILE("mobile"),

                          /** 平板设备 */
                          TABLET("tablet"),

                          /** IM服务器 */
                          SERVER("server");

        /**
        * 构造方法
        */
        private Terminal(String code) {
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

    /**
     * 操作系统
     * 
     * @author Rayliu40k
     * @version $Id: Os.java, v 0.1 2017年3月17日 下午1:16:44 Rayliu40k Exp $
     */
    public enum Os {

                    WINDOWS("windows"),

                    MAC("mac"),

                    ANDROID("android"),

                    IOS("ios"),

                    LINUX("linux");

        /**
                    * 构造方法
                    */
        private Os(String code) {
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

    /**
     * 传输协议
     * 
     * @author Rayliu40k
     * @version $Id: Transport.java, v 0.1 2017年3月17日 下午1:20:38 Rayliu40k Exp $
     */
    public enum Transport {

                           TCP("tcp"),

                           WEBSOCKET("websocket"),

                           HTTP("http");

        /**
        * 构造方法
        */
        private Transport(String code) {
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

    /**
     * 应用平台
     * 
     * @author Rayliu40k
     * @version $Id: Platform.java, v 0.1 2017年3月17日 下午1:28:59 Rayliu40k Exp $
     */
    public enum Platform {

                          /** 多客服 */
                          DKF("dkf"),

                          /** 微信 */
                          WECHAT("wechat"),

                          /** 移动app应用 */
                          APP("app"),

                          /** M站 */
                          MSITE("msite"),

                          /** 主站 */
                          HOME("home"),

                          /** IM服务器 */
                          SERVER("server");

        /**
        * 构造方法
        */
        private Platform(String code) {
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

    /**
     * 身份标识
     * 
     * @author Rayliu40k
     * @version $Id: UserIdentity.java, v 0.1 2017年3月17日 下午1:22:07 Rayliu40k Exp $
     */
    public enum UserIdentity {

                              /** 员工 */
                              STAFF("staff"),

                              /** 客户 */
                              CUSTOMER("customer");

        /**
        * 构造方法
        */
        private UserIdentity(String code) {
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

    /**
     * 强登录标识
     * 
     * @author Rayliu40k
     * @version $Id: Force.java, v 0.1 2017年3月17日 下午1:22:07 Rayliu40k Exp $
     */
    public enum Force {

                       TRUE("true"),

                       FALSE("false");

        /**
        * 构造方法
        */
        private Force(String code) {
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

    /**
     * 在线状态
     * 
     * @author Rayliu40k
     * @version $Id: Status.java, v 0.1 2017年3月17日 下午1:22:07 Rayliu40k Exp $
     */
    public enum Status {

                        /** 上线/在线 */
                        ONLINE("online"),

                        /** 下线/离线 */
                        OFFLINE("offline"),

                        /** 不可用 */
                        UNAVAILABLE("unavailable");

        /**
        * 构造方法
        */
        private Status(String code) {
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

    /**
     * 有效负载类型
     * 
     * @author Rayliu40k
     * @version $Id: ContentType.java, v 0.1 2017年3月17日 下午1:22:07 Rayliu40k Exp $
     */
    public enum ContentType {

                             /** 文本 */
                             TEXT("text"),

                             /** 图片 */
                             IMAGE("image"),

                             /** 语音 */
                             VOICE("voice"),

                             /** 视频 */
                             VIDEO("video");

        /**
        * 构造方法
        */
        private ContentType(String code) {
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

    /**
     * 系统通知类型
     * 
     * @author Rayliu40k
     * @version $Id: NotifyType.java, v 0.1 2017年3月17日 下午1:22:07 Rayliu40k Exp $
     */
    public enum NotifyType {

                            /** 用户下线请求 */
                            USER_OFFLINE_REQUEST("USER_OFFLINE_REQUEST"),

                            /** 分配客服请求 */
                            STAFF_ALLOCATE_REQUEST("STAFF_ALLOCATE_REQUEST"),

                            /** 分配客服成功 */
                            STAFF_ALLOCATE_SUCCESS("STAFF_ALLOCATE_SUCCESS"),

                            /** 分配客服失败 */
                            STAFF_ALLOCATE_FAIL("STAFF_ALLOCATE_FAIL");

        /**
        * 构造方法
        */
        private NotifyType(String code) {
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

    /**
     * 回执消息类型
     * 
     * @author Rayliu40k
     * @version $Id: AckType.java, v 0.1 2017年3月17日 下午1:22:07 Rayliu40k Exp $
     */
    public enum AckType {

                         /** 在线状态 */
                         PRESENCE("presence"),

                         /** 聊天  */
                         CHAT("chat"),

                         /** 聊天通知 */
                         CHAT_NOTIFICATION("chat_notification"),

                         /** 系统通知 */
                         SYSTEM_NOTIFICATION("system_notification");

        /**
        * 构造方法
        */
        private AckType(String code) {
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

    /***********************************************************************/
}
