package com.zkml.terminal.service.selfterminal.model;

import com.zkml.terminal.service.selfterminal.util.ASCIIUtil;
import lombok.Data;

/**
 * @Author: likun
 * @Date: Created in  2018/12/11 13:04
 * @Description:
 */
@Data
public class Message {

    private String sn; // 终端号
    private String version;// 版本号
    private String time;// 上传时间
    private Integer cpuUsageRate; // CPU占用率
    private Integer memoryUsageRate; // 内存用量
    private Integer companyId;// 单位ID
    private String url;// Web服务请求地址
    private String ip; // TCP服务的IP
    private Integer port; // TCP服务的端口号
    private String areaid;// 区域ID

    private String areaCode;// 区域区号
    private String messageId;// 消息ID
    private Integer messageBodyLength;// 消息体的长度
    private String checkCode;// 检验码
    private String messageBody;//消息体

    public Message(String command) {
        this.messageId = command.substring(0, 4);
        this.messageBodyLength = Integer.parseInt(command.substring(4, 8), 16);
        this.sn = ASCIIUtil.convertHexASCToStr(command.substring(8, 32));
        this.messageBody = command.substring(32, command.length() - 2);
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

}
