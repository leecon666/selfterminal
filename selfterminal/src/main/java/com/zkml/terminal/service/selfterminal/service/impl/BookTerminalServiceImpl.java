package com.zkml.terminal.service.selfterminal.service.impl;

import com.whalin.MemCached.MemCachedClient;
import com.zkml.terminal.service.selfterminal.dao.BookTerminalMapper;
import com.zkml.terminal.service.selfterminal.dto.BookTerminalDto;
import com.zkml.terminal.service.selfterminal.model.BookTerminal;
import com.zkml.terminal.service.selfterminal.model.Message;
import com.zkml.terminal.service.selfterminal.service.IBookTerminalService;
import com.zkml.terminal.service.selfterminal.thread.MessageConsumer;
import com.zkml.terminal.service.selfterminal.util.CommonUtil;
import com.zkml.terminal.service.selfterminal.util.MessageIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service("bookTerminalService")
public class BookTerminalServiceImpl implements IBookTerminalService {
    @Autowired
    private BookTerminalMapper bookTerminalMapper;
    @Autowired
    private MemCachedClient memCachedClient;
    @Autowired
    @Qualifier("messageConsumer")
    private MessageConsumer messageConsumer;
    // 创建延时队列
    DelayQueue<Message> queue = new DelayQueue<>();
    ExecutorService executorService = Executors.newFixedThreadPool(1);
    private static final SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public Map<String, String> queryBookTerminalBySn(String sn) {
        if (memCachedClient.keyExists(sn)) {
            Object obj = memCachedClient.get(sn);
            return (Map<String, String>) obj;
        }
        BookTerminal bookTerminal = bookTerminalMapper
                .selectBookTerminalBySn(sn);
        Map<String, String> resultMap = new HashMap<>();
        if (bookTerminal != null) {
            resultMap.put("type", bookTerminal.getType());
            resultMap.put("areaid", bookTerminal.getAreaid());
            resultMap.put("version", bookTerminal.getVersion());
            resultMap.put("ip", bookTerminal.getIp());
            resultMap.put("port", bookTerminal.getPort() + "");
            resultMap.put("url", bookTerminal.getUrl());
            resultMap.put("companyId", bookTerminal.getCompanyId());
            memCachedClient.add(sn, resultMap);
        } else {
            log.error("未查询到该终端号({})的配置信息", sn);
        }
        return resultMap;
    }
    @Transactional
    @Override
    public void settingTerminalParams(Message message) {
        String sn = CommonUtil.formatStr(message.getSn());
        String messageId = CommonUtil.formatStr(message.getMessageId());
        String version = CommonUtil.formatStr(message.getVersion());
        Integer cpuUsageRate = message.getCpuUsageRate();
        Integer memoryUsageRate = message.getMemoryUsageRate();
        String time = format.format(new Date());
        String url = CommonUtil.formatStr(message.getUrl());
        String ip = CommonUtil.formatStr(message.getIp());
        Integer port = message.getPort();
        String areaid = CommonUtil.formatStr(message.getAreaid());
        String companyId = message.getCompanyId();
        switch (messageId) {
            case MessageIdUtil.REPORT_ON_TIME:// 终端定时上报
            {
                Map<String, String> map = new HashMap<>();
                if (time != null && !time.equals("")) {
                    map.put("time", time);
                }
                if (version != null && !version.equals("")) {
                    map.put("version", version);
                }
                if (cpuUsageRate != null) {
                    map.put("cpuUsageRate", cpuUsageRate + "");
                }
                if (memoryUsageRate != null) {
                    map.put("memoryUsageRate", memoryUsageRate + "");
                }
                Object obj = memCachedClient.get(sn);
                if (obj != null) {
                    memCachedClient.set(sn, map);
                } else {
                    memCachedClient.add(sn, map);
                }
                BookTerminal b = bookTerminalMapper.selectBookTerminalBySn(sn);
                if (b != null) {
                    String oldVersion = b.getVersion();
                    if (version != null && !version.equals("")) {
                        String versionStr = CommonUtil.formatStr(version);
                        if (oldVersion != null && !oldVersion.equals("")) {
                            String oldVersionStr = CommonUtil.formatStr(oldVersion);
                            if (!versionStr.equals(oldVersionStr)) {
                                BookTerminalDto bookTerminalDto = new BookTerminalDto();
                                bookTerminalDto.setSn(sn);
                                bookTerminalDto.setVersion(versionStr);
                                int r1 = bookTerminalMapper.updateBookTerminal(bookTerminalDto);
                                if (r1 > 0) {
                                    log.info("终端({})版本设置成功", sn);
                                } else {
                                    log.info("终端({})版本设置失败", sn);
                                }
                            }
                        } else {
                            BookTerminalDto bookTerminalDto = new BookTerminalDto();
                            bookTerminalDto.setVersion(versionStr);
                            bookTerminalDto.setSn(sn);
                            int r2 = bookTerminalMapper.updateBookTerminal(bookTerminalDto);
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
                    BookTerminalDto bookTerminalDto = new BookTerminalDto();
                    bookTerminalDto.setSn(sn);
                    bookTerminalDto.setStatus(0);//在线
                    int result = bookTerminalMapper.updateBookTerminal(bookTerminalDto);
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
            }
            case MessageIdUtil.REPLY_QUERY_PARAMETERS:// 查询终端参数应答
            {
                BookTerminalDto bookTerminalDto = new BookTerminalDto();
                bookTerminalDto.setSn(sn);
                if (url != null && !url.equals("")) {
                    bookTerminalDto.setUrl(url);
                }
                if (ip != null && !sn.equals("")) {
                    bookTerminalDto.setIp(ip);
                }
                if (port != null) {
                    bookTerminalDto.setPort(port);
                }
                if (areaid != null && !areaid.equals("")) {
                    if (areaid.indexOf("ppp") != -1 || areaid.indexOf("ccc") != -1) {
                        areaid = areaid.substring(0, areaid.length() - 3);
                    }
                    bookTerminalDto.setAreaid(areaid);
                }
                if (companyId != null && !companyId.equals("")) {
                    bookTerminalDto.setCompanyId(companyId);
                }
                int result = bookTerminalMapper.updateBookTerminal(bookTerminalDto);
                if (result > 0) {
                    log.info("终端({})修改成功", sn);
                } else {
                    log.info("终端({})修改失败", sn);
                }
                log.info("自助终端（{}）Web服务请求地址（{}）ip地址（{}）端口号（{}）单位编号({})区域ID({})", sn, url, ip, port, companyId, areaid);
                break;
            }
            case MessageIdUtil.GENERAL_RESPONSE:// 终端通用应答
            {
                if (message.isFlag()) {
                    String settingKey = "setting" + sn;
                    if (memCachedClient.keyExists(settingKey)) {
                        Object o = memCachedClient.get(settingKey);
                        Map<String, String> map = (Map<String, String>) o;
                        String psign = map.get("psign");
                        BookTerminalDto bookTerminalDto = new BookTerminalDto();
                        bookTerminalDto.setSn(sn);
                        if (psign != null && !psign.equals("")) {
                            bookTerminalDto.setPsign(psign);
                        }
                        int column = bookTerminalMapper.updateBookTerminal(bookTerminalDto);
                        if (column > 0) {
                            log.info("终端({})修改库标识成功", sn);
                        } else {
                            log.info("终端({})修改库标识失败", sn);
                        }
                    }

                }
                break;
            }
            default:
                break;
        }
    }
}
