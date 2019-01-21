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
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
        if (selfTerminal != null) {
            map.put("areaid", selfTerminal.getAreaid());
            map.put("version", selfTerminal.getVersion());
            map.put("ip", selfTerminal.getIp());
            map.put("port", selfTerminal.getPort() + "");
            map.put("url", selfTerminal.getUrl());
            map.put("companyId", selfTerminal.getCompanyId());
            map.put("type", selfTerminal.getType());
            memCachedClient.add(sn, map);
        }
        return map;
    }

    /**
     * @Description:终端参数设置
     * @Method: com.zkml.terminal.service.selfterminal.service.impl.SelfTerminalServiceImpl.updateByPrimaryKeySelective
     * @Author: likun
     * @Date: 2018/12/11 11:58
     * @param: message
     */
    @Transactional
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
        String companyId = message.getCompanyId();
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
                SelfTerminal s = selfTerminalMapper.querySelfTerminalBySn(sn);
                if (s != null) {
                    String oldVersion = s.getVersion();
                    if (version != null && !version.equals("")) {
                        String versionStr = CommonUtil.formatStr(version);
                        if (oldVersion != null && !oldVersion.equals("")) {
                            String oldVersionStr = CommonUtil.formatStr(oldVersion);
                            if (!versionStr.equals(oldVersionStr)) {
                                SelfTerminal selfTerminal1 = new SelfTerminal();
                                selfTerminal1.setSn(sn);
                                selfTerminal1.setVersion(versionStr);
                                int r1 = selfTerminalMapper.updateByPrimaryKeySelective(selfTerminal1);
                                if (r1 > 0) {
                                    log.info("终端({})版本设置成功", sn);
                                } else {
                                    log.info("终端({})版本设置失败", sn);
                                }
                            }
                        } else {
                            SelfTerminal selfTerminal6 = new SelfTerminal();
                            selfTerminal6.setVersion(versionStr);
                            selfTerminal6.setSn(sn);
                            int r2 = selfTerminalMapper.updateByPrimaryKeySelective(selfTerminal6);
                            if (r2 > 0) {
                                log.info("终端({})版本设置成功", sn);
                            } else {
                                log.info("终端({})版本设置失败", sn);
                            }
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
            case MessageIdUtil.REPLY_QUERY_PARAMETERS:// 查询终端参数应答
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
                    if (areaid.indexOf("ppp") != -1 || areaid.indexOf("ccc") != -1) {
                        areaid = areaid.substring(0, areaid.length() - 3);
                    }
                    selfTerminal2.setAreaid(areaid);
                }
                if (companyId != null && !companyId.equals("")) {
                    selfTerminal2.setCompanyId(companyId);
                }
                int result = selfTerminalMapper.updateByPrimaryKeySelective(selfTerminal2);
                if (result > 0) {
                    log.info("终端({})修改成功", sn);
                } else {
                    log.info("终端({})修改失败", sn);
                }
                log.info("自助终端（{}）Web服务请求地址（{}）ip地址（{}）端口号（{}）单位编号({})区域ID({})", sn, url, ip, port, companyId, areaid);
                break;
            case MessageIdUtil.GENERAL_RESPONSE:// 终端通用应答
                if (message.isFlag()) {
                    String settingKey = "setting" + sn;
                    if (memCachedClient.keyExists(settingKey)) {
                        Object o = memCachedClient.get(settingKey);
                        Map<String, String> map = (Map<String, String>) o;
                        String psign = map.get("psign");
                        SelfTerminal selfTerminal3 = new SelfTerminal();
                        selfTerminal3.setSn(sn);
                        if (psign != null && !psign.equals("")) {
                            selfTerminal3.setPsign(psign);
                        }
                        int column = selfTerminalMapper.updateByPrimaryKeySelective(selfTerminal3);
                        if (column > 0) {
                            log.info("终端({})修改库标识成功", sn);
                        } else {
                            log.info("终端({})修改库标识失败", sn);
                        }
                    }

                }
                break;
            default:
                break;
        }
    }
}
