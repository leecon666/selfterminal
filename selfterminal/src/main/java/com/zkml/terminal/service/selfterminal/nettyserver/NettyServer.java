package com.zkml.terminal.service.selfterminal.nettyserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: likun
 * @Date: Created in  2018/12/10 12:43
 * @Description:netty服务端
 */
@SpringBootApplication
@MapperScan("com.zkml.terminal.service.selfterminal.dao")
@EnableAutoConfiguration
@Slf4j
public class NettyServer {
    public static void main(String[] args) {
        SpringApplication.run(NettyServer.class, args);
        //bossGroup线程监听端口，workerGroup线程负责数据读写
        EventLoopGroup bossGroup = null;
        EventLoopGroup workerGroup = null;
        try {
            log.info("启动自助终端服务");
            LinkedBlockingQueue<Runnable> linkedBlockingQueue = new LinkedBlockingQueue<Runnable>();
            final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(100, 1000, 3000, TimeUnit
                    .MILLISECONDS, linkedBlockingQueue);
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            // 保持连接数
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024 * 1024);
            // 有数据立即发送
            serverBootstrap.option(ChannelOption.TCP_NODELAY, true);
            // 保持连接
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            // 处理新连接
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    // 增加任务处理
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast("decoder", new MessageDecoder());
                    pipeline.addLast(new NettyServerHandler(threadPoolExecutor));
                }
            });
            // 绑定端口,开始接收进来的连接
            ChannelFuture channelFuture = serverBootstrap.bind(31292).sync();
            // 等待服务器socket关闭
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("netty服务启动异常" + e.getMessage());
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
