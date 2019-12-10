package com.zkml.terminal.service.selfterminal.service;

import com.zkml.terminal.service.selfterminal.model.Message;

import java.util.Map;

public interface IBookTerminalService {
    Map<String, String> queryBookTerminalBySn(String sn);

    void settingTerminalParams(Message message);
}
