package com.zkml.terminal.service.selfterminal.util;

import com.zkml.terminal.service.selfterminal.model.Message;

/**
 * @Author: likun
 * @Date: Created in  2018/12/11 16:58
 * @Description:
 */
public class ParseMessageUtil {
    /**
     * @Description:
     * @Method: parseMessage
     * @Author: likun
     * @Date: 2018/12/11 17:00
     * @param:
     */
    public static void parseMessage(Message message) {
        String messageBody = message.getMessageBody();
        Integer messageBodyLength = message.getMessageBodyLength();
        switch (message.getMessageId()) {
            case MessageIdUtil.REPORT_ON_TIME:// 终端定时上报
                String version =messageBody.substring(0,8);
                String time=messageBody.substring(8,20);
                String extraInfo=messageBody.substring(20,2*(messageBodyLength-10)+20);
                message.setTime(time);
                message.setVersion(version);
                break;
            case MessageIdUtil.GENERAL_RESPONSE:// 终端通用应答
                String messageId =messageBody.substring(0,4);
                break;
            case MessageIdUtil.QUERY_PARAMETERS://查询终端参数
                messageBody = messageBody.substring(2);
                break;
            case MessageIdUtil.TERMINAL_CONTROL:// 终端控制
                break;
            default:
                break;
        }
    }
}
