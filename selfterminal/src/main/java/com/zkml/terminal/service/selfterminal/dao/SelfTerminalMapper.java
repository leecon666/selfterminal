package com.zkml.terminal.service.selfterminal.dao;

import com.zkml.terminal.service.selfterminal.model.SelfTerminal;
import org.apache.ibatis.annotations.Param;


/**
 * @Author: likun
 * @Date: Created in  2018/12/11 10:43
 * @Description:
 */
public interface SelfTerminalMapper {
    SelfTerminal querySelfTerminalBySn(@Param("sn") String sn);

    int updateByPrimaryKeySelective(SelfTerminal selfTerminal);
}
