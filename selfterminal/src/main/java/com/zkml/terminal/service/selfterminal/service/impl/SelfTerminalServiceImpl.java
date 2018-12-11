package com.zkml.terminal.service.selfterminal.service.impl;

import com.whalin.MemCached.MemCachedClient;
import com.zkml.terminal.service.selfterminal.dao.SelfTerminalMapper;
import com.zkml.terminal.service.selfterminal.model.Message;
import com.zkml.terminal.service.selfterminal.model.SelfTerminal;
import com.zkml.terminal.service.selfterminal.service.ISelfTerminalService;
import com.zkml.terminal.service.selfterminal.util.MessageIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("selfTerminalService")
@Slf4j
public class SelfTerminalServiceImpl implements ISelfTerminalService {
    @Autowired
    private SelfTerminalMapper selfTerminalMapper;
    @Autowired
    private static MemCachedClient memCachedClient;

    /**
     * @Description:根据终端号查询终端参数
     * @Method: com.zkml.terminal.service.selfterminal.service.impl.SelfTerminalServiceImpl.querySelfTerminalBySn
     * @Author: likun
     * @Date: 2018/12/11 10:57
     * @param: sn终端号
     */
    @Override
    public Map<String, String> querySelfTerminalBySn(String sn) {
        if (memCachedClient != null && memCachedClient.keyExists(sn)) {
            Object obj = memCachedClient.get(sn);
            return (Map<String, String>) obj;
        }
        SelfTerminal selfTerminal = selfTerminalMapper.querySelfTerminalBySn(sn);
        Map<String, String> map = new HashMap<String, String>();
        map.put("areaid", selfTerminal.getAreaid());
        map.put("version", selfTerminal.getVersion());
        map.put("ip", selfTerminal.getIp());
        map.put("port", selfTerminal.getPort() + "");
        map.put("url", selfTerminal.getUrl());
        map.put("cpuUsageRate", "0");
        map.put("memoryUsageRate", "0");
        map.put("time", "");
        memCachedClient.add(sn, map);
        return map;
    }

    /**
     * @Description:终端参数设置
     * @Method: com.zkml.terminal.service.selfterminal.service.impl.SelfTerminalServiceImpl.updateByPrimaryKeySelective
     * @Author: likun
     * @Date: 2018/12/11 10:58
     * @param: message
     */
    @Override
    public int settingTerminalParams(Message message) {
        String sn = message.getSn();
        String messageId = message.getMessageId();
        String version = message.getVersion();
        String cpuUsageRate = message.getCpuUsageRate();
        String memoryUsageRate = message.getMemoryUsageRate();
        String time = message.getTime();
        String url = message.getUrl();
        String ip = message.getIp();
        Integer port = message.getPort();
        Object obj = memCachedClient.get(sn);
        switch (messageId) {
            case MessageIdUtil.REPORT_ON_TIME:// 终端定时上报
                if (obj != null) {
                    Map<String, String> resultMap = (Map<String, String>) obj;
                    if (cpuUsageRate != null && !cpuUsageRate.equals("")) {
                        resultMap.put("cpuUsageRate", cpuUsageRate);
                    }
                    if (memoryUsageRate != null && !memoryUsageRate.equals("")) {
                        resultMap.put("memoryUsageRate", memoryUsageRate);
                    }
                    if (time != null && !time.equals("")) {
                        resultMap.put("time", time);
                    }
                    if (version != null && !version.equals("")) {
                        resultMap.put("version", version);
                    }
                    memCachedClient.set(sn, resultMap);
                }
                log.info("自助终端（{}）时间（{}）CPU占用率（{}） 内存用量（{}）版本（{}）", sn, time, cpuUsageRate, memoryUsageRate, version);
                break;
            case MessageIdUtil.GENERAL_RESPONSE://终端通用应答
                break;
            case MessageIdUtil.REPLY_QUERY_PARAMETERS:// 查询终端参数应答
                if (obj != null) {
                    Map<String, String> resultMap = (Map<String, String>) obj;
                    if (url != null && !url.equals("")) {
                        resultMap.put("url", url);
                    }
                    if (ip != null && !ip.equals("")) {
                        resultMap.put("ip", ip);
                    }
                    if (port != null) {
                        resultMap.put("port", port + "");
                    }
                    memCachedClient.set(sn, resultMap);
                }
                log.info("自助终端（{}）Web服务请求地址（{}） ip地址（{}） 端口号（{}）", sn, url, ip, port);
                break;
            default:
                break;
        }
        return 0;
    }
}
