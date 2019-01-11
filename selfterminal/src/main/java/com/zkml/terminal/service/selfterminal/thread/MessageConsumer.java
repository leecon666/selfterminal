package com.zkml.terminal.service.selfterminal.thread;

import com.zkml.terminal.service.selfterminal.dao.SelfTerminalMapper;
import com.zkml.terminal.service.selfterminal.model.Message;
import com.zkml.terminal.service.selfterminal.model.SelfTerminal;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.DelayQueue;

/**
 * @Author: likun
 * @Date: Created in  2018/12/21 14:02
 * @Description:消息消费者
 */
@Data
@Slf4j
@Component
@Qualifier("messageConsumer")
public class MessageConsumer implements Runnable {
    @Autowired
    private SelfTerminalMapper selfTerminalMapper;
    // 延时队列 ,消费者从其中获取消息进行消费
    private DelayQueue<Message> queue;

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Message msg = queue.take();
                if (msg != null) {
                    SelfTerminal selfTerminal = new SelfTerminal();
                    selfTerminal.setSn(msg.getSn());
                    selfTerminal.setStatus(1);//离线
                    int result = selfTerminalMapper.updateByPrimaryKeySelective(selfTerminal);
                    if (result > 0) {
                        log.info("终端({})修改状态成功", msg.getSn());
                    } else {
                        log.info("终端({})修改状态失败", msg.getSn());
                    }
                    SelfTerminalServiceThread.map.remove(msg.getSn());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
