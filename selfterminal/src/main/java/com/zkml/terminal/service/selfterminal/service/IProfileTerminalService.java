package com.zkml.terminal.service.selfterminal.service;

import com.zkml.terminal.service.selfterminal.model.Message;

import java.util.Map;

/**
 * @Author: likun
 * @Date: Created in  2019/2/27 14:34
 * @Description:
 */
public interface IProfileTerminalService {
    Map<String, String> queryProfileTerminalBySn(String sn);

    void settingTerminalParams(Message message);
}
