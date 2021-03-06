package com.zkml.terminal.service.selfterminal.service;

import com.zkml.terminal.service.selfterminal.model.Message;

import java.util.Map;


public interface ISelfTerminalService {
    Map<String, String> querySelfTerminalBySn(String sn);

    void settingTerminalParams(Message message);
}
