package com.zkml.terminal.service.selfterminal.thread;

import com.whalin.MemCached.MemCachedClient;
import com.zkml.terminal.service.selfterminal.dao.SelfTerminalMapper;
import com.zkml.terminal.service.selfterminal.model.Message;
import com.zkml.terminal.service.selfterminal.service.ISelfTerminalService;
import com.zkml.terminal.service.selfterminal.util.MessageIdUtil;
import com.zkml.terminal.service.selfterminal.util.ParseMessageUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: likun
 * @Date: Created in  2018/12/10 10:44
 * @Description:自助终端服务线程
 */
@Slf4j
@Data
@Component
@Qualifier("selfTerminalServiceThread")
public class SelfTerminalServiceThread implements Runnable {
    @Autowired
    @Qualifier("selfTerminalService")
    private ISelfTerminalService selfTerminalService;
    @Autowired
    private MemCachedClient memCachedClient;
    @Autowired
    private SelfTerminalMapper selfTerminalMapper;
    private String command;
    private ChannelHandlerContext ctx;
    public static Map<String, Channel> map = new ConcurrentHashMap<>();//解决多线程冲突
    @Override
    public void run() {
        log.info("接收终端发来的指令({})", this.command);
        if (command != null && !command.equals("")) {
            command = command.substring(4);
            Message message = new Message(command, 10); // 添加延时消息,延时10分钟
            String sn = message.getSn();
            if (sn != null && !sn.equals("")) {
                Map<String, String> resultMap = selfTerminalService.querySelfTerminalBySn(sn);
                if (resultMap != null && !resultMap.isEmpty()) {
                    map.put(sn,ctx.channel());
                    switch (message.getMessageId()) {
                        case MessageIdUtil.GENERAL_RESPONSE://终端通用应答
                            ParseMessageUtil.parseMessage(message);
                            selfTerminalService.settingTerminalParams(message);
                            break;
                        case MessageIdUtil.REPORT_ON_TIME:// 终端定时上报
                            log.info("终端({})定时上报", sn);
                            ParseMessageUtil.parseMessage(message);
                            selfTerminalService.settingTerminalParams(message);
                            break;
                        case MessageIdUtil.REPLY_QUERY_PARAMETERS:// 查询终端参数应答
                            log.info("查询终端({})参数应答", sn);
                            ParseMessageUtil.parseMessage(message);
                            selfTerminalService.settingTerminalParams(message);
                            break;
                        default:
                            break;
                    }
                } else {
                    log.error("未查到({})终端的配置信息", sn);
                }
            } else {
                log.error("从接收的指令中未得到终端号");
            }
        }
    }
}
