package com.meilele.im.gateway.transport.server.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.meilele.common.model.FacadeInvokeResult;
import com.meilele.common.util.UUIDGenerator;
import com.meilele.im.gateway.constants.Constants;
import com.meilele.im.gateway.enums.ErrorCode;
import com.meilele.im.gateway.transport.server.http.handler.PlatformInBound;

import io.netty.util.CharsetUtil;

/**
 * Http服务协议
 * 
 * @author fengbo1
 * @version $Id: HttpTransportServer.java, v 0.1 2017年4月5日 上午9:11:18 fengbo1 Exp $
 */
@Controller
@RequestMapping("api/service")
public class PlatformTransportServer {

    /** 日志 */
    private static final Logger logger = LoggerFactory.getLogger(PlatformTransportServer.class);

    @Autowired
    private PlatformInBound     httpMessageHandler;

    /**
     * 接收消息 
     * 
     * @param request {@link HttpServletRequest}
     */
    @RequestMapping(value = "/receiverMessage", method = RequestMethod.POST)
    @ResponseBody
    public FacadeInvokeResult receiverMessage(HttpServletRequest request) {
        MDC.put(Constants.TRACE_ID, UUIDGenerator.getUUID());
        String rawPacket = receivePost(request);
        logger.info("【接收到http消息---->{}】", rawPacket);
        if (StringUtils.isBlank(rawPacket)) {
            logger.error("【接收原始消息为空】");
            return FacadeInvokeResult.failure(ErrorCode.INVALID_PARAMETER.getCode(),
                ErrorCode.INVALID_PARAMETER.getDesc());
        }

        try {
            httpMessageHandler.in(rawPacket);
        } catch (Exception e) {
            logger.error("【接收消息处理失败】", e);
            return FacadeInvokeResult.failure(e);
        } finally {
            MDC.remove(Constants.TRACE_ID);
        }

        return FacadeInvokeResult.success();
    }

    /**
     * 获取请求参数
     * 
     * @return json
     */
    public String receivePost(HttpServletRequest request) {
        // 读取请求内容
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            // 将数据解码
            String reqBody = sb.toString();
            return URLDecoder.decode(reqBody, CharsetUtil.UTF_8.name());
        } catch (IOException e) {
            logger.error("转换出错", e);
        }
        return null;
    }
}
