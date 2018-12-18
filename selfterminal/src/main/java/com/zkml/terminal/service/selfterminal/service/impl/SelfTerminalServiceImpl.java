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
    private MemCachedClient memCachedClient;

    /**
     * @Description:根据终端号查询终端参数
     * @Method: com.zkml.terminal.service.selfterminal.service.impl.SelfTerminalServiceImpl.querySelfTerminalBySn
     * @Author: likun
     * @Date: 2018/12/11 10:57
     * @param: sn终端号
     */
    @Override
    public Map<String, String> querySelfTerminalBySn(String sn) {
        if (memCachedClient != null) {
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
            map.put("cpuUsageRate", "0");
            map.put("memoryUsageRate", "0");
            map.put("time", "");
            memCachedClient.add(sn, map);
            return map;
        }
        return null;
    }

    /**
     * @Description:终端参数设置
     * @Method: com.zkml.terminal.service.selfterminal.service.impl.SelfTerminalServiceImpl.updateByPrimaryKeySelective
     * @Author: likun
     * @Date: 2018/12/11 10:58
     * @param: message
     */
    @Override
    public void settingTerminalParams(Message message) {
        String sn = message.getSn();
        String messageId = message.getMessageId();
        String version = message.getVersion();
        Integer cpuUsageRate = message.getCpuUsageRate();
        Integer memoryUsageRate = message.getMemoryUsageRate();
        String time = message.getTime();
        String url = message.getUrl();
        String ip = message.getIp();
        Integer port = message.getPort();
        String areaid = message.getAreaid();
        Integer companyId = message.getCompanyId();
        if (memCachedClient != null) {
            Object obj = memCachedClient.get(sn);
            switch (messageId) {
                case MessageIdUtil.REPORT_ON_TIME:// 终端定时上报
                    if (obj != null) {
                        Map<String, String> resultMap = (Map<String, String>) obj;
                        if (cpuUsageRate != null) {
                            resultMap.put("cpuUsageRate", cpuUsageRate.toString());
                        }
                        if (memoryUsageRate != null) {
                            resultMap.put("memoryUsageRate", memoryUsageRate.toString());
                        }
                        if (time != null && !time.equals("")) {
                            resultMap.put("time", time);
                        }
                        if (version != null && !version.equals("")) {
                            resultMap.put("version", version);
                        }
                        memCachedClient.set(sn, resultMap);
                    }
                    log.info("自助终端（{}）时间（{}）CPU占用率（{}%） 内存用量（{}M）版本（{}）", sn, time, cpuUsageRate, memoryUsageRate,
                            version);
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
                            resultMap.put("port", port.toString());
                        }
                        memCachedClient.set(sn, resultMap);
                    }
                    log.info("自助终端（{}）Web服务请求地址（{}）ip地址（{}）端口号（{}）", sn, url, ip, port);
                    break;
                default:
                    break;
            }
            SelfTerminal selfTerminal = new SelfTerminal();
            selfTerminal.setSn(sn);
            selfTerminal.setVersion(version);
            selfTerminal.setUrl(url);
            selfTerminal.setIp(ip);
            selfTerminal.setPort(port);
            selfTerminal.setAreaid(areaid);
            selfTerminal.setCompanyId(companyId);
            int result = selfTerminalMapper.updateByPrimaryKeySelective(selfTerminal);
            if (result > 0) {
                log.info("终端({})参数设置成功", sn);
            } else {
                log.info("终端({})参数设置失败", sn);
            }
        }
    }
}
