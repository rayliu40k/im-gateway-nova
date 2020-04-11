/**
 * meilele.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.meilele.im.gateway.transport.server.http.result;

import lombok.Data;

/**
 * 微信返回数据对象
 * 
 * @author fengbo1
 * @version $Id: HttpResult.java, v 0.1 2016年8月31日 下午7:37:13 fengbo1 Exp $
 */
@Data
public class HttpResult {

    /** 是否成功 */
    private boolean success;

    /** 错误码 **/
    private String  errorCode;

    /** 错误消息 */
    private String  errorMessage;

}
