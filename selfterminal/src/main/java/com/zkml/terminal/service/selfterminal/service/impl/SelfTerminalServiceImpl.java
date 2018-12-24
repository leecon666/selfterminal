package com.zkml.terminal.service.selfterminal.service.impl;

import com.whalin.MemCached.MemCachedClient;
import com.zkml.terminal.service.selfterminal.dao.SelfTerminalMapper;
import com.zkml.terminal.service.selfterminal.model.Message;
import com.zkml.terminal.service.selfterminal.model.SelfTerminal;
import com.zkml.terminal.service.selfterminal.service.ISelfTerminalService;
import com.zkml.terminal.service.selfterminal.thread.MessageConsumer;
import com.zkml.terminal.service.selfterminal.util.CommonUtil;
import com.zkml.terminal.service.selfterminal.util.MessageIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service("selfTerminalService")
@Slf4j
public class SelfTerminalServiceImpl implements ISelfTerminalService {
    @Autowired
    private SelfTerminalMapper selfTerminalMapper;
    @Autowired
    private MemCachedClient memCachedClient;
    @Autowired
    @Qualifier("messageConsumer")
    private MessageConsumer messageConsumer;
    // 创建延时队列
    DelayQueue<Message> queue = new DelayQueue<>();
    ExecutorService executorService = Executors.newFixedThreadPool(1);

    /**
     * @Description:根据终端号查询终端参数
     * @Method: com.zkml.terminal.service.selfterminal.service.impl.SelfTerminalServiceImpl.querySelfTerminalBySn
     * @Author: likun
     * @Date: 2018/12/11 10:57
     * @param: sn终端号
     */
    @Override
    public Map<String, String> querySelfTerminalBySn(String sn) {
        if (memCachedClient.keyExists(sn)) {
            Object obj = memCachedClient.get(sn);
            return (Map<String, String>) obj;
        }
        SelfTerminal selfTerminal = selfTerminalMapper.querySelfTerminalBySn(sn);
        Map<String, String> map = new HashMap<>();
        map.put("areaid", selfTerminal.getAreaid());
        map.put("version", selfTerminal.getVersion());
        map.put("ip", selfTerminal.getIp());
        map.put("port", selfTerminal.getPort().toString());
        map.put("url", selfTerminal.getUrl());
        map.put("cpuUsageRate", "");
        map.put("memoryUsageRate", "");
        map.put("time", "");
        memCachedClient.add(sn, map);
        return map;
    }

    /**
     * @Description:终端参数设置
     * @Method: com.zkml.terminal.service.selfterminal.service.impl.SelfTerminalServiceImpl.updateByPrimaryKeySelective
     * @Author: likun
     * @Date: 2018/12/11 11:58
     * @param: message
     */
    @Override
    public void settingTerminalParams(Message message) {
        String sn = CommonUtil.formatStr(message.getSn());
        String messageId = CommonUtil.formatStr(message.getMessageId());
        String version = CommonUtil.formatStr(message.getVersion());
        Integer cpuUsageRate = message.getCpuUsageRate();
        Integer memoryUsageRate = message.getMemoryUsageRate();
        String time = message.getTime();
        String url = CommonUtil.formatStr(message.getUrl());
        String ip = CommonUtil.formatStr(message.getIp());
        Integer port = message.getPort();
        String areaid = CommonUtil.formatStr(message.getAreaid());
        Integer companyId = message.getCompanyId();
        Object obj = memCachedClient.get(sn);
        switch (messageId) {
            case MessageIdUtil.REPORT_ON_TIME:// 终端定时上报
                if (obj != null) {
                    Map<String, String> map = (Map<String, String>) obj;
                    if (cpuUsageRate != null) {
                        map.put("cpuUsageRate", cpuUsageRate.toString());
                    }
                    if (memoryUsageRate != null) {
                        map.put("memoryUsageRate", memoryUsageRate.toString());
                    }
                    if (time != null && !time.equals("")) {
                        map.put("time", time);
                    }
                    if (version != null && !version.equals("")) {
                        map.put("version", version);
                    }
                    memCachedClient.set(sn, map);
                }
                String oldVersion = selfTerminalMapper.querySelfTerminalBySn(sn).getVersion();
                String versionStr = CommonUtil.formatStr(version);
                String oldVersionStr = CommonUtil.formatStr(oldVersion);
                if (!versionStr.equals("") && !oldVersionStr.equals("")) {
                    if (!versionStr.equals(oldVersionStr)) {
                        SelfTerminal selfTerminal1 = new SelfTerminal();
                        selfTerminal1.setSn(sn);
                        selfTerminal1.setVersion(versionStr);
                        int result = selfTerminalMapper.updateByPrimaryKeySelective(selfTerminal1);
                        if (result > 0) {
                            log.info("终端({})版本设置成功", sn);
                        } else {
                            log.info("终端({})版本设置失败", sn);
                        }
                    }
                }
                log.info("自助终端（{}）时间（{}）CPU占用率（{}%） 内存用量（{}M）版本（{}）", sn, time, cpuUsageRate, memoryUsageRate,
                        version);
                boolean flag = true;
                for (Message m : queue) {
                    if (m.getSn().equals(message.getSn())) {
                        flag = false;
                        queue.remove(m);
                    }
                }
                if (flag) {
                    SelfTerminal selfTerminal = new SelfTerminal();
                    selfTerminal.setSn(sn);
                    selfTerminal.setStatus(0);//在线
                    int result = selfTerminalMapper.updateByPrimaryKeySelective(selfTerminal);
                    if (result > 0) {
                        log.info("终端({})修改状态成功", sn);
                    } else {
                        log.info("终端({})修改状态失败", sn);
                    }
                }
                //将延时消息放到延时队列中
                queue.offer(message);
                messageConsumer.setQueue(queue);
                executorService.execute(messageConsumer);
                break;
            case MessageIdUtil.GENERAL_RESPONSE://终端通用应答
                break;
            case MessageIdUtil.REPLY_QUERY_PARAMETERS:// 查询终端参数应答
//                if (obj != null) {
//                    Map<String, String> map = (Map<String, String>) obj;
//                    if (url != null && !url.equals("")) {
//                        map.put("url", url);
//                    }
//                    if (ip != null && !ip.equals("")) {
//                        map.put("ip", ip);
//                    }
//                    if (port != null) {
//                        map.put("port", port.toString());
//                    }
//                    memCachedClient.set(sn, map);
//                }
                SelfTerminal selfTerminal2 = new SelfTerminal();
                selfTerminal2.setSn(sn);
                if (url != null && !url.equals("")) {
                    selfTerminal2.setUrl(url);
                }
                if (ip != null && !sn.equals("")) {
                    selfTerminal2.setIp(ip);
                }
                if (port != null) {
                    selfTerminal2.setPort(port);
                }
                if (areaid != null && !areaid.equals("")) {
                    selfTerminal2.setAreaid(areaid);
                }
                if (companyId != null) {
                    selfTerminal2.setCompanyId(companyId);
                }
                int result = selfTerminalMapper.updateByPrimaryKeySelective(selfTerminal2);
                if (result > 0) {
                    log.info("终端({})参数设置成功", sn);
                } else {
                    log.info("终端({})参数设置失败", sn);
                }
                log.info("自助终端（{}）Web服务请求地址（{}）ip地址（{}）端口号（{}）", sn, url, ip, port);
                break;
            default:
                break;
        }
    }
}
