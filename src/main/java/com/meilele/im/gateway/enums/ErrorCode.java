/**
 * Sunnysoft.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.meilele.im.gateway.enums;

/**
 * 异常代码枚举
 * 
 * @author Rayliu40k
 * @version $Id: ErrorCode.java, v 0.1 2017年3月10日 下午4:39:51 Rayliu40k Exp $
 */
public enum ErrorCode {

                       INVALID_PARAMETER("invalid_parameter", "非法参数"),

                       OBJECT_IS_NULL("object_is_null", "对象为空"),

                       INVALID_PROTOCOL_FORMAT("invalid_protocol_format", "非法协议格式"),

                       INVALID_EVENT_ON_PHASE("invalid_event_in_phase", "非法事件"),

                       EVENT_EXECUTION_ERROR("event_execution_error", "事件执行异常"),

                       USER_ALREADY_KICK_OFF("user_already_kick_off", "当前用户已经被强制下线"),

                       IO_ERROR("io_error", "IO异常"),

                       UNDEFINED_ERROR("undefined_error", "未知异常");

    /** 异常代码 */
    private String code;

    /** 异常描述 */
    private String desc;

    /**
     * 构造器
     * @param code 异常代码
     * @param desc 异常描述
     */
    private ErrorCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

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

    /**
     * Getter method for property <tt>desc</tt>.
     * 
     * @return property value of desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Setter method for property <tt>desc</tt>.
     * 
     * @param desc value to be assigned to property desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

}