package com.meilele.im.gateway.transport.server.tcp.handler;

import java.util.List;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.meilele.im.gateway.enums.ErrorCode;
import com.meilele.im.gateway.exception.ProtocolValidationException;
import com.meilele.im.gateway.ndp.model.NDPDefinition;
import com.meilele.im.gateway.ndp.model.NDPPacket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * Json报文到NDP报文对象解码器
 * 
 * @author Rayliu40k
 * @version $Id: JsonPacket2NDPPacketDecoder.java, v 0.1 2017年3月10日 下午2:59:59 Rayliu40k Exp $
 */
public class JsonPacket2NDPPacketDecoder extends MessageToMessageDecoder<String> {

    /** 日志 */
    private static final Logger logger = LoggerFactory.getLogger(JsonPacket2NDPPacketDecoder.class);

    /**
     * @see io.netty.handler.codec.MessageToMessageDecoder#decode(io.netty.channel.ChannelHandlerContext, java.lang.Object, java.util.List)
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, String json,
                          List<Object> out) throws Exception {
        NDPPacket packet = new Gson().fromJson(json, NDPPacket.class);
        this.validate(packet);
        out.add(packet);
    }

    /**
     * @see io.netty.channel.ChannelHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof ProtocolValidationException) {
            logger.error("【Json报文到NDP报文校验异常】", cause);
            logger.error("【关闭连接】Channel：{}", ctx.channel());
            ctx.channel().close();
        } else {
            ctx.fireExceptionCaught(cause);
        }
    }

    /**
     * NDP协议完整性、合法性校验
     * 
     * @param packet {@link NDPPacket}
     */
    private void validate(NDPPacket packet) {
        //协议必选头部校验
        if (!StringUtils.equals(packet.getVersion(), NDPDefinition.VERSION_NUMBER)) {
            logger.error("【报文校验异常】version：{}", packet.getVersion());
            throw new ProtocolValidationException(ErrorCode.INVALID_PROTOCOL_FORMAT, "非法协议格式");
        }
        if (StringUtils.isEmpty(packet.getId())) {
            logger.error("【报文校验异常】id为空");
            throw new ProtocolValidationException(ErrorCode.INVALID_PROTOCOL_FORMAT, "非法协议格式");
        }
        String type = StringUtils.trim(packet.getType());
        if (StringUtils.isEmpty(type)
            || !EnumUtils.isValidEnum(NDPDefinition.Type.class, type.toUpperCase())) {
            logger.error("【报文校验异常】无效type:{}", type);
            throw new ProtocolValidationException(ErrorCode.INVALID_PROTOCOL_FORMAT, "非法协议格式");
        }
        String terminal = StringUtils.trim(packet.getTerminal());
        if (StringUtils.isEmpty(terminal)
            || !EnumUtils.isValidEnum(NDPDefinition.Terminal.class, terminal.toUpperCase())) {
            logger.error("【报文校验异常】无效terminal:{}", terminal);
            throw new ProtocolValidationException(ErrorCode.INVALID_PROTOCOL_FORMAT, "非法协议格式");
        }
        String os = StringUtils.trim(packet.getOs());
        if (StringUtils.isEmpty(os)
            || !EnumUtils.isValidEnum(NDPDefinition.Os.class, os.toUpperCase())) {
            logger.error("【报文校验异常】无效os:{}", os);
            throw new ProtocolValidationException(ErrorCode.INVALID_PROTOCOL_FORMAT, "非法协议格式");
        }
        String transport = StringUtils.trim(packet.getTransport());
        if (StringUtils.isEmpty(transport)
            || !EnumUtils.isValidEnum(NDPDefinition.Transport.class, transport.toUpperCase())) {
            logger.error("【报文校验异常】无效transport:{}", transport);
            throw new ProtocolValidationException(ErrorCode.INVALID_PROTOCOL_FORMAT, "非法协议格式");
        }
        String platform = StringUtils.trim(packet.getPlatform());
        if (StringUtils.isEmpty(platform)
            || !EnumUtils.isValidEnum(NDPDefinition.Platform.class, platform.toUpperCase())) {
            logger.error("【报文校验异常】无效platform:{}", platform);
            throw new ProtocolValidationException(ErrorCode.INVALID_PROTOCOL_FORMAT, "非法协议格式");
        }
        //协议有效负载校验
        //当type为presence类型
        if (StringUtils.equals(type, NDPDefinition.Type.PRESENCE.getCode())) {
            String from = packet.getBody().get(NDPDefinition.FROM);
            if (StringUtils.isEmpty(from)) {
                logger.error("【报文校验异常】from为空");
                throw new ProtocolValidationException(ErrorCode.INVALID_PROTOCOL_FORMAT, "非法协议格式");
            }
            String userIdentity = packet.getBody().get(NDPDefinition.USER_IDENTITY);
            if (StringUtils.isEmpty(userIdentity) || !EnumUtils
                .isValidEnum(NDPDefinition.UserIdentity.class, userIdentity.toUpperCase())) {
                logger.error("【报文校验异常】无效userIdentity:{}", userIdentity);
                throw new ProtocolValidationException(ErrorCode.INVALID_PROTOCOL_FORMAT, "非法协议格式");
            }
            String status = packet.getBody().get(NDPDefinition.STATUS);
            if (StringUtils.isEmpty(status)
                || !EnumUtils.isValidEnum(NDPDefinition.Status.class, status.toUpperCase())) {
                logger.error("【报文校验异常】无效status:{}", status);
                throw new ProtocolValidationException(ErrorCode.INVALID_PROTOCOL_FORMAT, "非法协议格式");
            }
            String force = packet.getBody().get(NDPDefinition.FORCE);
            if (StringUtils.isEmpty(force)
                || !EnumUtils.isValidEnum(NDPDefinition.Force.class, force.toUpperCase())) {
                logger.error("【报文校验异常】无效force:{}", force);
                throw new ProtocolValidationException(ErrorCode.INVALID_PROTOCOL_FORMAT, "非法协议格式");
            }
        }
        //当type为chat
        if (StringUtils.equals(type, NDPDefinition.Type.CHAT.getCode())
            || StringUtils.equals(type, NDPDefinition.Type.CHAT_NOTIFICATION.getCode())) {
            String from = packet.getBody().get(NDPDefinition.FROM);
            if (StringUtils.isEmpty(from)) {
                logger.error("【报文校验异常】from为空");
                throw new ProtocolValidationException(ErrorCode.INVALID_PROTOCOL_FORMAT, "非法协议格式");
            }
            String contentType = packet.getBody().get(NDPDefinition.CONTENT_TYPE);
            if (StringUtils.isEmpty(contentType) || !EnumUtils
                .isValidEnum(NDPDefinition.ContentType.class, contentType.toUpperCase())) {
                logger.error("【报文校验异常】无效contentType:{}", contentType);
                throw new ProtocolValidationException(ErrorCode.INVALID_PROTOCOL_FORMAT, "非法协议格式");
            }
        }
        //当type为system_notification类型
        if (StringUtils.equals(type, NDPDefinition.Type.SYSTEM_NOTIFICATION.getCode())) {
            //有system_notification协议太灵活，情况很多，故只校验notifyType
            String notifyType = packet.getBody().get(NDPDefinition.NOTIFY_TYPE);
            if (StringUtils.isEmpty(notifyType) || !EnumUtils
                .isValidEnum(NDPDefinition.NotifyType.class, notifyType.toUpperCase())) {
                logger.error("【报文校验异常】无效notifyType:{}", notifyType);
                throw new ProtocolValidationException(ErrorCode.INVALID_PROTOCOL_FORMAT, "非法协议格式");
            }
        }
        //当type为ack类型
        if (StringUtils.equals(type, NDPDefinition.Type.ACK.getCode())) {
            String ackId = packet.getBody().get(NDPDefinition.ACK_ID);
            if (StringUtils.isEmpty(ackId)) {
                logger.error("【报文校验异常】ackId为空");
                throw new ProtocolValidationException(ErrorCode.INVALID_PROTOCOL_FORMAT, "非法协议格式");
            }
            String ackType = packet.getBody().get(NDPDefinition.ACK_TYPE);
            if (StringUtils.isEmpty(ackType)
                || !EnumUtils.isValidEnum(NDPDefinition.AckType.class, ackType.toUpperCase())) {
                logger.error("【报文校验异常】无效ackType:{}", ackType);
                throw new ProtocolValidationException(ErrorCode.INVALID_PROTOCOL_FORMAT, "非法协议格式");
            }
        }
    }
}
