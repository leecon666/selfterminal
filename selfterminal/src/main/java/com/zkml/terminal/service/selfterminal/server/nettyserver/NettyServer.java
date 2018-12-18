package com.zkml.terminal.service.selfterminal.server.nettyserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * @Author: likun
 * @Date: Created in  2018/12/10 12:43
 * @Description:netty服务端
 */
@Component
@Slf4j
public class NettyServer {
    @Autowired
    @Qualifier("serverBootstrap")
    private ServerBootstrap serverBootstrap;
    @Autowired
    @Qualifier("tcpSocketAddress")
    private InetSocketAddress tcpSocketAddress;
    @Autowired
    @Qualifier("bossGroup")
    private NioEventLoopGroup bossGroup;
    @Autowired
    @Qualifier("workerGroup")
    private NioEventLoopGroup workerGroup;

    public void startNettyServer() throws Exception {
        // 绑定端口,开始接收进来的连接
        ChannelFuture channelFuture = serverBootstrap.bind(tcpSocketAddress).sync();
        // 等待服务器socket关闭
        channelFuture.channel().closeFuture().sync();
    }

    @PreDestroy
    public void close() {
        log.info("关闭服务器....");
        //优雅退出
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
