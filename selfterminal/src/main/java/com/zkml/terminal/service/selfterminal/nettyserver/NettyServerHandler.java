package com.zkml.terminal.service.selfterminal.nettyserver;

import com.zkml.terminal.service.selfterminal.thread.SelfTerminalServiceThread;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author: likun
 * @Date: Created in  2018/12/10 14:01
 * @Description:
 */
@ChannelHandler.Sharable
@Slf4j
public class NettyServerHandler extends ChannelHandlerAdapter {
    private ThreadPoolExecutor threadPoolExecutor;

    public NettyServerHandler(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg != null && !msg.equals("")) {
            threadPoolExecutor.execute(new SelfTerminalServiceThread(msg.toString(), ctx));
        } else {
            log.error("接收的指令为空");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端关闭（{}）", cause.getMessage());
        ctx.close();
    }
}
