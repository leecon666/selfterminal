package com.zkml.terminal.service.selfterminal.util;

import com.zkml.terminal.service.selfterminal.model.Message;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
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
                String extraInfo = messageBody.substring(20);//附加信息
                Map<String, String> map = parseExtraInfo(extraInfo);
                if (!CollectionUtils.isEmpty(map)) {
                    if (map.containsKey("01")) {
                        message.setCpuUsageRate(ASCIIUtil.convertHexStrToString(map.get("01")));
                    }
                    if (map.containsKey("02")) {
                        message.setMemoryUsageRate(ASCIIUtil.convertHexStrToString(map.get("02")));
                    }
                }
                message.setTime(time);
                message.setVersion(version);
                break;
            case MessageIdUtil.GENERAL_RESPONSE:// 终端通用应答
                String messageId = messageBody.substring(0, 4);
                String messageName = getMessageNameByMsgId(messageId);
                Integer result = Integer.parseInt(messageBody.substring(4, 6), 16);
                StringBuilder builder = new StringBuilder("");
                builder.append("终端号为").append(message.getSn()).append(",").append(messageName);
                if (result == 0) {
                    builder.append("成功");
                } else if (result == 1) {
                    builder.append("失败");
                } else if (result == 2) {
                    builder.append("消息有误");
                } else if (result == 3) {
                    builder.append("不支持");
                }
                log.info(builder.toString());
                break;
            case MessageIdUtil.REPLY_QUERY_PARAMETERS://查询终端参数应答
                messageBody = messageBody.substring(2);
                while (messageBody.length() > 0) {
                    int paramId = Integer.parseInt(messageBody.substring(0, 2), 16);
                    int paramlength = Integer.parseInt(messageBody.substring(2, 4), 16);
                    String description = messageBody.substring(4, 4 + 2 * paramlength);
                    switch (paramId) {
                        case 1:
                            String areaCode = ASCIIUtil.convertHexStrToString(description);
                            message.setAreaCode(areaCode);
                            break;
                        case 2:
                            Integer companyId = Integer.parseInt(description, 16);
                            message.setCompanyId(companyId);
                            break;
                        case 3:
                            String url = ASCIIUtil.convertHexStrToString(description);
                            message.setUrl(url);
                            break;
                        case 4:
                            String ip = ASCIIUtil.convertHexStrToString(description);
                            message.setIp(ip);
                            break;
                        case 5:
                            Integer port = Integer.parseInt(description, 16);
                            message.setPort(port);
                            break;
                        case 7:
                            String areaid = ASCIIUtil.convertHexStrToString(description);
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

    public static String parseTime(String time) {
        StringBuilder sb = new StringBuilder("");
        if (time != null && !time.equals("")) {
            time = time.replaceAll("(.{2})", "$1 ").trim();//每两位插入空格
            String[] str = time.split(" ");
            sb.append("20").append(str[0]).append("-").append(str[1]).append("-").append(str[2]).append("-").append
                    (str[3]).append("-").append(str[4]).append("-").append(str[5]);
        }
        return sb.toString();
    }
/**
 * @Description:解析附加信息
 * @Method: com.zkml.terminal.service.selfterminal.util.ParseMessageUtil.parseExtraInfo
 * @Author: likun
 * @Date: 2018/12/13 9:27
 * @param: extraInfo
 */
    public static Map<String, String> parseExtraInfo(String extraInfo) {
        Map<String, String> map = new HashMap<String, String>();
        while (extraInfo != null && !extraInfo.equals("")) {
            String commandId = extraInfo.substring(0, 2);
            int length = Integer.parseInt(extraInfo.substring(2, 4), 16);
            String message = extraInfo.substring(4, 4 + length * 2);
            map.put(commandId, message);
            extraInfo = extraInfo.substring(4 + length * 2);
        }
        return map;
    }

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

    public static void sendMessage(ChannelHandlerContext ctx, String msg, String sn) {
        ChannelFuture future = ctx.channel().close();
        //服务端发送数据完毕后,关闭通道
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    log.info("终端号({})发送({})指令成功", msg, sn);
                } else {
                    log.error("终端号({})发送({})指令失败", msg, sn);
                }
            }
        });
    }
}
