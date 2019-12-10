package com.zkml.terminal.service.selfterminal.dao;

import com.zkml.terminal.service.selfterminal.dto.BookTerminalDto;
import com.zkml.terminal.service.selfterminal.model.BookTerminal;
import org.apache.ibatis.annotations.Param;

public interface BookTerminalMapper {

    int updateBookTerminal(BookTerminalDto bookTerminalDto);

    BookTerminal selectBookTerminalBySn(@Param("sn") String sn);
}
