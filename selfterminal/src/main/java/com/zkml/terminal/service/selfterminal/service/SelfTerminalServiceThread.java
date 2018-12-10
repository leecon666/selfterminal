package com.zkml.terminal.service.selfterminal.service;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: likun
 * @Date: Created in  2018/12/10 10:44
 * @Description:自助终端服务线程
 */
@Slf4j
public class SelfTerminalServiceThread implements Runnable {
    private String command;
    private ChannelHandlerContext ctx;

    public SelfTerminalServiceThread(String command, ChannelHandlerContext ctx) {
        this.command = command;
        this.ctx = ctx;
    }

    @Override
    public void run() {
        log.info("接受终端发来的指令({})", this.command);
        if (command != null && !command.equals("")) {
            command=command.substring(4);

        } else {
            log.error("从接收的指令中未获取到终端号");
        }
    }
}
