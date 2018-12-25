package com.zkml.terminal.service.selfterminal.server.config;

import com.zkml.terminal.service.selfterminal.server.entity.NettyConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author: likun
 * @Date: Created in  2018/12/18 11:13
 * @Description:
 */
@Component
public class NettyTcpConfig {
    @Autowired
    private NettyConfig nettyConfig;
    @Autowired
    @Qualifier("nettyInitializer")
    private NettyInitializer nettyInitializer;

    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup(nettyConfig.getBossThread());
    }

    @Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(nettyConfig.getWorkerThread());
    }

    @Bean(name = "tcpSocketAddress")
    public InetSocketAddress tcpPort() {
        return new InetSocketAddress(nettyConfig.getTcpPort());
    }

    @Bean(name = "serverBootstrap")
    public ServerBootstrap bootstrap() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup(), workerGroup());
        serverBootstrap.channel(NioServerSocketChannel.class);
        // 处理新连接
        serverBootstrap.childHandler(nettyInitializer);
        Map<ChannelOption<?>, Object> tcpChannelOptions = tcpChannelOptions();
        Set<ChannelOption<?>> keySet = tcpChannelOptions.keySet();
        for (
                ChannelOption option : keySet) {
            serverBootstrap.option(option, tcpChannelOptions.get(option));
        }
        return serverBootstrap;
    }

    @Bean(name = "tcpChannelOptions")
    public Map<ChannelOption<?>, Object> tcpChannelOptions() {
        Map<ChannelOption<?>, Object> options = new HashMap<>();
        // 保持连接
        options.put(ChannelOption.SO_KEEPALIVE, nettyConfig.isKeepAlive());
        // 保持连接数
        options.put(ChannelOption.SO_BACKLOG, nettyConfig.getBackLog());
        // 有数据立即发送
        options.put(ChannelOption.TCP_NODELAY, nettyConfig.isNodelay());
        //地址复用
        options.put(ChannelOption.SO_REUSEADDR,nettyConfig.isReuseAddr());
        //TCP数据发送缓冲区大小
        options.put(ChannelOption.SO_SNDBUF,nettyConfig.getSndBuf());
        //TCP数据接收缓冲区大小
        options.put(ChannelOption.SO_RCVBUF,nettyConfig.getRevBuf());
        return options;
    }
}
