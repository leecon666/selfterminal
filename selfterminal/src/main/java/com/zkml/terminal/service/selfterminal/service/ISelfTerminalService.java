package com.zkml.terminal.service.selfterminal.service;

import com.zkml.terminal.service.selfterminal.model.Message;

import java.util.Map;


public interface ISelfTerminalService {
    /**
     * @Description:根据终端号查询终端参数
     * @Method: com.zkml.terminal.service.selfterminal.service.ISelfTerminalService.querySelfTerminalBySn
     * @Author: likun
     * @Date: 2018/12/11 10:57
     * @param: sn终端号
     */
    Map<String,String> querySelfTerminalBySn(String sn);

    /**
     * @Description:终端参数设置
     * @Method: com.zkml.terminal.service.selfterminal.service.ISelfTerminalService.settingTerminalParams
     * @Author: likun
     * @Date: 2018/12/11 10:58
     * @param: parseMessageUtil
     */
    int settingTerminalParams(Message message);
}
