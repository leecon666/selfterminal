package com.zkml.terminal.service.selfterminal.dao;


import com.zkml.terminal.service.selfterminal.model.SelfTerminalMold;
import org.apache.ibatis.annotations.Param;

public interface SelfTerminalMoldMapper {
    /**
     * @Description:根据终端号查询自助终端类型
     * @Method: com.zkml.terminal.service.selfterminal.dao.SelfTerminalMoldMapper.selectBySn
     * @Author: likun
     * @Date: 2019/2/27 14:03
     * @param sn 终端号
     */
    SelfTerminalMold selectBySn(@Param("sn") String sn);

}