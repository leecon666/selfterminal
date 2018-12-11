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
                break;
            case MessageIdUtil.GENERAL_RESPONSE:// 终端通用应答
                break;
            case MessageIdUtil.QUERY_PARAMETERS://查询终端参数
                break;
            case MessageIdUtil.TERMINAL_CONTROL:// 终端控制
                break;
            default:
                break;
        }
    }
}
