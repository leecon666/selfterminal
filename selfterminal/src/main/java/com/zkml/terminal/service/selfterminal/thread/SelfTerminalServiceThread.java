package com.zkml.terminal.service.selfterminal.thread;

import com.zkml.terminal.service.selfterminal.model.Message;
import com.zkml.terminal.service.selfterminal.service.ISelfTerminalService;
import com.zkml.terminal.service.selfterminal.util.MessageIdUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;

/**
 * @Author: likun
 * @Date: Created in  2018/12/10 10:44
 * @Description:自助终端服务线程
 */
@Slf4j
public class SelfTerminalServiceThread implements Runnable {
    @Autowired
    @Qualifier("selfTerminalService")
    private ISelfTerminalService selfTerminalService;
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
            command = command.substring(4);
            Message message = new Message(command);
            String sn = message.getSn();
            if (sn != null && !sn.equals("")) {
                Map<String, String> resultMap = selfTerminalService.querySelfTerminalBySn(sn);
                if (resultMap != null && !resultMap.isEmpty()) {
                    switch (message.getMessageId()) {
                        case MessageIdUtil.REPORT_ON_TIME:// 终端定时上报
                            break;
                        case MessageIdUtil.GENERAL_RESPONSE://终端通用应答
                            break;
                        case MessageIdUtil.REPLY_QUERY_PARAMETERS:// 查询终端参数应答
                            break;
                        default:
                            break;
                    }
                } else {
                    log.error("未查到({})终端的配置信息", sn);
                }
            } else {
                log.error("从接收的指令中未获取到终端号");
            }
        }
    }
}
