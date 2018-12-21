package com.zkml.terminal.service.selfterminal.server.handler;

import com.zkml.terminal.service.selfterminal.thread.SelfTerminalServiceThread;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * @Author: likun
 * @Date: Created in  2018/12/10 14:01
 * @Description:
 */
@Component
@ChannelHandler.Sharable
@Qualifier("nettyServerHandler")
@Slf4j
public class NettyServerHandler extends ChannelHandlerAdapter {
    @Autowired
    @Qualifier("selfTerminalServiceThread")
    private SelfTerminalServiceThread selfTerminalServiceThread;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ExecutorService executorService=Executors.newSingleThreadExecutor();
        selfTerminalServiceThread.setCommand(msg.toString());
        selfTerminalServiceThread.setCtx(ctx);
        if (msg != null && !msg.toString().equals("")) {
            executorService.execute(selfTerminalServiceThread);
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
