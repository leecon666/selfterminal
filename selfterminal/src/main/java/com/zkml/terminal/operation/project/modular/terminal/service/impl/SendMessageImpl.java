package com.zkml.terminal.operation.project.modular.terminal.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zkml.terminal.operation.project.modular.terminal.service.ISendMessage;
import com.zkml.terminal.service.selfterminal.thread.SelfTerminalServiceThread;
import com.zkml.terminal.service.selfterminal.util.ParseMessageUtil;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;
/**
 * @Author: likun
 * @Date: Created in  2019/1/8 13:15
 * @Description:
 */
@Service(interfaceClass = ISendMessage.class)
@Component
public class SendMessageImpl implements ISendMessage {
    @Override
    public void sendMessage(String sn, String message) {
        Channel ctx = SelfTerminalServiceThread.map.get(sn);
        ParseMessageUtil.sendMessage(ctx, message, sn);
    }
}
