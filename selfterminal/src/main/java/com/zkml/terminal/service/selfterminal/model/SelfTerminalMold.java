package com.zkml.terminal.service.selfterminal.model;

import lombok.Data;

/**
 * @Author: likun
 * @Date: Created in  2019/2/27 13:58
 * @Description:自助终端类型实体类
 */
@Data
public class SelfTerminalMold {
    private Integer id;
    /**
     * 终端号
     */
    private String sn;
    /**
     * 自助终端类型(调度、档案、会议、签批)
     */
    private String type;

}