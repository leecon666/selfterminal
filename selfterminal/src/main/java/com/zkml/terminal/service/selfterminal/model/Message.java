package com.zkml.terminal.service.selfterminal.model;

import com.zkml.terminal.service.selfterminal.util.ASCIIUtil;
import lombok.Data;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @Author: likun
 * @Date: Created in  2018/12/11 13:04
 * @Description:
 */
@Data
public class Message implements Delayed {

    private String sn; // 终端号
    private String version;// 版本号
    private String time;// 上传时间
    private Integer cpuUsageRate; // CPU占用率
    private Integer memoryUsageRate; // 内存用量
    private String companyId;// 单位ID
    private String url;// Web服务请求地址
    private String ip; // TCP服务的IP
    private Integer port; // TCP服务的端口号
    private String areaid;// 区域ID
    private String areaCode;// 区域区号
    private String messageId;// 消息ID
    private Integer messageBodyLength;// 消息体的长度
    private String checkCode;// 检验码
    private String messageBody;//消息体
    private boolean flag=false;
    private long activeTime;// 延迟时长，这个是必须的属性因为要按照这个判断延时时长

    public Message(String command, long activeTime) {
        this.messageId = command.substring(0, 4);
        this.messageBodyLength = Integer.parseInt(command.substring(4, 8), 16);
        this.sn = ASCIIUtil.convertHexASCToStr(command.substring(8, 32));
        this.messageBody = command.substring(32, command.length() - 2);
        this.activeTime = TimeUnit.NANOSECONDS.convert(activeTime, TimeUnit.MINUTES) + System.nanoTime();
    }

    public void setVersion(String version) {
        StringBuilder sb = new StringBuilder();
        if (version != null && !version.equals("")) {
            String[] versionArr = version.replaceAll("(.{2})", "$1 ").trim().split(" ");
            sb.append(Integer.parseInt(versionArr[0], 16)).append(".").append(Integer.parseInt(versionArr[1], 16))
                    .append(".").append(Integer.parseInt(versionArr[2], 16)).append(".").append(Integer.parseInt
                    (versionArr[3], 16));
        }
        this.version = sb.toString();
    }

    @Override
    public long getDelay(TimeUnit unit) {// 延迟任务是否到时就是按照这个方法判断如果返回的是负数则说明到期否则还没到期
        return unit.convert(this.activeTime - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed delayed) {
        long diff = getDelay(TimeUnit.NANOSECONDS) - delayed.getDelay(TimeUnit.NANOSECONDS);
        return diff > 0 ? 1 : -1;
    }
}
