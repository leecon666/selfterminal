package com.zkml.terminal.service.selfterminal.thread;

import com.whalin.MemCached.MemCachedClient;
import com.zkml.terminal.service.selfterminal.dao.ProfileTerminalMapper;
import com.zkml.terminal.service.selfterminal.dao.SelfTerminalMapper;
import com.zkml.terminal.service.selfterminal.dao.SelfTerminalMoldMapper;
import com.zkml.terminal.service.selfterminal.model.Message;
import com.zkml.terminal.service.selfterminal.model.ProfileTerminal;
import com.zkml.terminal.service.selfterminal.model.SelfTerminal;
import com.zkml.terminal.service.selfterminal.model.SelfTerminalMold;
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
    @Autowired
    private SelfTerminalMoldMapper selfTerminalMoldMapper;
    @Autowired
    private ProfileTerminalMapper profileTerminalMapper;
    @Autowired
    private MemCachedClient memCachedClient;

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Message msg = queue.take();
                if (msg != null) {
                    String sn = msg.getSn();
                    if (sn != null & !sn.equals("")) {
                        String moldKey = "mold" + sn;
                        Object obj = memCachedClient.get(moldKey);
                        String type = "";
                        if (obj != null) {
                            type = (String) obj;
                        } else {
                            SelfTerminalMold selfTerminalMold = selfTerminalMoldMapper.selectBySn(sn);
                            if (selfTerminalMold != null) {
                                type = selfTerminalMold.getType();
                                memCachedClient.add(moldKey, type);
                            } else {
                                log.error("未查到({})终端号的终端类型信息", sn);
                            }
                        }
                        if (type != null && !type.equals("")) {
                            switch (type) {
                                case "dispatch": {//调度
                                    SelfTerminal selfTerminal = new SelfTerminal();
                                    selfTerminal.setSn(sn);
                                    selfTerminal.setStatus(1);//离线
                                    int result = selfTerminalMapper.updateByPrimaryKeySelective(selfTerminal);
                                    if (result > 0) {
                                        log.info("终端({})修改状态成功", sn);
                                    } else {
                                        log.info("终端({})修改状态失败", sn);
                                    }
                                    SelfTerminalServiceThread.map.remove(sn);
                                    break;
                                }
                                case "profile": {//档案
                                    ProfileTerminal profileTerminal = new ProfileTerminal();
                                    profileTerminal.setSn(sn);
                                    profileTerminal.setStatus(1);//离线
                                    int result = profileTerminalMapper.updateProfileTerminal(profileTerminal);
                                    if (result > 0) {
                                        log.info("终端({})修改状态成功", sn);
                                    } else {
                                        log.info("终端({})修改状态失败", sn);
                                    }
                                    SelfTerminalServiceThread.map.remove(sn);
                                    break;
                                }
                                case "meeting": {//会议
                                    break;
                                }
                                case "sign": {//签批
                                    break;
                                }
                                default:
                                    break;
                            }
                        } else {
                            log.error("未查询到该终端({})的终端类型", sn);
                        }
                    }
                }
            } catch (InterruptedException e) {
                log.error("消息消费者出现异常了" + e.getMessage());
            }
        }
    }
}
