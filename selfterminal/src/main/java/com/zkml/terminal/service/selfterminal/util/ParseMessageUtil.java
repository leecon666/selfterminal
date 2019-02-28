package com.zkml.terminal.service.selfterminal.util;

import com.zkml.terminal.service.selfterminal.model.Message;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: likun
 * @Date: Created in  2018/12/11 16:58
 * @Description:
 */
@Slf4j
public class ParseMessageUtil {
    /**
     * @Description:
     * @Method: com.zkml.terminal.service.selfterminal.util.ParseMessageUtil.parseMessage
     * @Author: likun
     * @Date: 2018/12/11 17:00
     * @param:message解析消息
     */
    public static void parseMessage(Message message) {
        String messageBody = message.getMessageBody();
        Integer messageBodyLength = message.getMessageBodyLength();
        switch (message.getMessageId()) {
            case MessageIdUtil.REPORT_ON_TIME:// 终端定时上报
                String version = messageBody.substring(0, 8);
                String time = parseTime(messageBody.substring(8, 20));
                String extraInfo = messageBody.substring(20, 2 * messageBodyLength);//附加信息
                Map<String, String> map = parseExtraInfo(extraInfo);
                if (!CollectionUtils.isEmpty(map)) {
                    if (map.containsKey("01")) {
                        message.setCpuUsageRate(Integer.parseInt(map.get("01"), 16));
                    }
                    if (map.containsKey("02")) {
                        message.setMemoryUsageRate(Integer.parseInt(map.get("02"), 16));
                    }
                }
                message.setTime(time);
                message.setVersion(version);
                break;
            case MessageIdUtil.GENERAL_RESPONSE:// 终端通用应答
                String messageId = messageBody.substring(0, 4);
                String messageName = getMessageNameByMsgId(messageId);
                Integer result = Integer.parseInt(messageBody.substring(4, 6), 16);
                StringBuilder sb = new StringBuilder("");
                String sn = message.getSn();
                sb.append("终端号为").append(sn).append(",").append(messageName);
                switch (result) {
                    case 0:
                        sb.append("成功");
                        break;
                    case 1:
                        sb.append("失败");
                        break;
                    case 2:
                        sb.append("消息有误");
                        break;
                    case 3:
                        sb.append("不支持");
                        break;
                    default:
                        break;
                }
                if (messageId.equals(MessageIdUtil.SETTING_PARAMETERS) && result == 0) {
                    message.setFlag(true);
                }
                log.info(sb.toString());
                break;
            case MessageIdUtil.REPLY_QUERY_PARAMETERS://查询终端参数应答
                messageBody = messageBody.substring(2);
                while (messageBody.length() > 0) {
                    int paramId = Integer.parseInt(messageBody.substring(0, 2), 16);
                    int paramlength = Integer.parseInt(messageBody.substring(2, 4), 16);
                    String description = messageBody.substring(4, 4 + 2 * paramlength);
                    switch (paramId) {
                        case 1:
                            message.setAreaCode(description);
                            break;
                        case 2:
                            String companyId = CommonUtil.parseHexString(description);
                            message.setCompanyId(companyId);
                            break;
                        case 3:
                            String url = CommonUtil.parseHexString(description);
                            message.setUrl(url);
                            break;
                        case 4:
                            String ip = CommonUtil.parseHexString(description);
                            message.setIp(ip);
                            break;
                        case 5:
                            Integer port = Integer.parseInt(description, 16);
                            message.setPort(port);
                            break;
                        case 7:
                            String areaid = CommonUtil.parseHexString(description);
                            message.setAreaid(areaid);
                            break;
                        default:
                            break;
                    }
                    messageBody = messageBody.substring(4 + 2 * paramlength);
                }
                break;
            default:
                break;
        }
    }

    /**
     * @Description:解析附加信息
     * @Method: com.zkml.terminal.service.selfterminal.util.ParseMessageUtil.parseExtraInfo
     * @Author: likun
     * @Date: 2018/12/13 9:27
     * @param: extraInfo
     */
    public static Map<String, String> parseExtraInfo(String extraInfo) {
        Map<String, String> map = new HashMap<>();
        while (extraInfo != null && !extraInfo.equals("")) {
            String commandId = extraInfo.substring(0, 2);
            int length = Integer.parseInt(extraInfo.substring(2, 4), 16);
            String message = extraInfo.substring(4, 4 + length * 2);
            extraInfo = extraInfo.substring(4 + length * 2);
            map.put(commandId, message);
        }
        return map;
    }

    /**
     * @Description:根据消息ID获取消息名称
     * @Method: com.zkml.terminal.service.selfterminal.util.ParseMessageUtil.getMessageNameByMsgId
     * @Author: likun
     * @Date: 2018/12/17 19:31
     * @param: messageId消息ID
     */
    public static String getMessageNameByMsgId(String messageId) {
        String messageName = null;
        switch (messageId) {
            case "0001":
                messageName = "终端通用应答";
                break;
            case "0200":
                messageName = "终端定时上报";
                break;
            case "8102":
                messageName = "查询终端状态";
                break;
            case "8103":
                messageName = "设置终端参数";
                break;
            case "8104":
                messageName = "查询终端参数";
                break;
            case "0104":
                messageName = "查询终端参数应答";
                break;
            case "8105":
                messageName = "终端控制";
                break;
            case "8106":
                messageName = "车辆VIN请求";
                break;
            case "8107":
                messageName = "车辆检测报告推送";
                break;
            default:
                messageName = "未知消息";
                break;
        }
        return messageName;
    }

    /**
     * @Description:发送消息
     * @Method: com.zkml.terminal.service.selfterminal.util.ParseMessageUtil.sendMessage
     * @Author: likun
     * @Date: 2018/12/17 19:31
     * @param: ctx环境
     * @param: msg消息
     * @param: sn终端号
     */
    public static void sendMessage(Channel ctx, String msg, String sn) {
        ChannelFuture channelFuture = ctx.writeAndFlush(Unpooled.copiedBuffer(hexStringToBytes(msg)));
        channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if (future.isSuccess()) {
                    log.info("终端号({})发送({})指令成功", sn, msg);
                } else {
                    log.error("终端号({})发送({})指令失败", sn, msg);
                }
            }
        });
    }

    /**
     * @Description:解析时间
     * @Method: com.zkml.terminal.service.selfterminal.util.ParseMessageUtil.parseTime
     * @Author: likun
     * @Date: 2018/12/18 13:45
     * @param: time
     */
    public static String parseTime(String time) {
        StringBuilder sb = new StringBuilder("");
        if (time != null && !time.equals("")) {
            time = time.replaceAll("(.{2})", "$1 ").trim();//每两位插入空格
            String[] str = time.split(" ");
            sb.append("20").append(str[0]).append("-").append(str[1]).append("-").append(str[2]).append(" ").append
                    (str[3]).append(":").append(str[4]).append(":").append(str[5]);
        }
        return sb.toString();
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

}
