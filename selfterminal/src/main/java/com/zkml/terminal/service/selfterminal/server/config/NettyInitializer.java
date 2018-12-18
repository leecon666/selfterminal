package com.zkml.terminal.service.selfterminal.server.config;

import com.zkml.terminal.service.selfterminal.server.handler.MessageDecoder;
import com.zkml.terminal.service.selfterminal.server.handler.NettyServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @Author: likun
 * @Date: Created in  2018/12/18 11:08
 * @Description:
 */
@Component
@Qualifier("nettyInitializer")
public class NettyInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    @Qualifier("nettyServerHandler")
    private NettyServerHandler nettyServerHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline channelPipeline = socketChannel.pipeline();
        channelPipeline.addLast("decoder", new MessageDecoder())
                .addLast(nettyServerHandler);
    }
}
