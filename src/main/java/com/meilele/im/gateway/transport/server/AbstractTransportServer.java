package com.meilele.im.gateway.transport.server;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.meilele.im.gateway.constants.Constants;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 网关传输服务器抽象类
 * 
 * @author Rayliu40k
 * @version $Id: AbstractTransportServer.java, v 0.1 2017年3月10日 下午1:45:52 Rayliu40k Exp $
 */
public abstract class AbstractTransportServer implements TransportServer {

    /** 日志 */
    private static final Logger               logger          = LoggerFactory
        .getLogger(AbstractTransportServer.class);

    /** Netty包报文日志处理器 */
    private static final LoggingHandler       LOGGING_HANDLER = new LoggingHandler(LogLevel.INFO);

    /** 网关在线用户统计定时任务 */
    @Autowired
    private OnlineUserStatisticsScheduledTask onlineUserstatisticsScheduledTask;

    /**
     * @see com.meilele.im.gateway.TransportServer.server.ProtocolServer#start()
     */
    @Override
    public void start() {
        //初始化
        this.init();
        //实例化主线程和工作线程
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        //服务器启动
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .localAddress(this.getTransportServerConfig().getAddress(),
                    this.getTransportServerConfig().getPort())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        //添加Netty日志Handler
                        channel.pipeline().addLast(Constants.LOGGING_HANDLER, LOGGING_HANDLER);
                        //注册ChannelHandler
                        addChannelHandlers(channel);
                    }
                }).childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true);
            //监听客户端连接
            ChannelFuture f = bootstrap.bind().sync();
            logger.info("【启动传输服务器】服务器:{},监听端口:{}", this.getTransportServerConfig().getServerName(),
                this.getTransportServerConfig().getPort());
            //监听服务器关闭事件
            f.channel().closeFuture().sync();
            logger.warn("【传输服务器关闭】服务器:{}", this.getTransportServerConfig().getServerName());
        } catch (Exception e) {
            logger.error("【[ERR]传输服务器发生异常】服务器:" + this.getTransportServerConfig().getServerName(),
                e);
        } finally {
            //优雅退出
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 初始化
     */
    protected void init() {
        //在线用户统计任务（每10s统计一次）
        ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1);
        scheduler.scheduleAtFixedRate(this.onlineUserstatisticsScheduledTask, 5, 10,
            TimeUnit.SECONDS);
    }

    /**
     * 具体传输服务器子类自行添加各自ChannelHandler
     * 
     * @param channel {@link SocketChannel}
     */
    protected void addChannelHandlers(SocketChannel channel) {
    };

    /**
     * 获取传输服务器
     * 
     * @return {@link TransportServerConfig}
     */
    protected TransportServerConfig getTransportServerConfig() {
        return null;
    }

}
