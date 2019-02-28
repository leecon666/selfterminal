package com.zkml.terminal.service.selfterminal.dao;


import com.zkml.terminal.service.selfterminal.model.ProfileTerminal;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: likun
 * @Date: Created in  2019/2/25 14:18
 * @Description:
 */
public interface ProfileTerminalMapper {

    int updateProfileTerminal(ProfileTerminal profileTerminal);

    ProfileTerminal selectProfileTerminalBySn(@Param("sn") String sn);
}