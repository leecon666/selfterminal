package com.zkml.terminal.service.selfterminal.model;

import lombok.Data;

@Data
public class BookTerminal {
    private Integer id;

    private String sn;

    private String type;

    private String version;

    private Integer status;

    private String createTime;

    private String updateTime;

    private String cpuUsageRate;

    private String memoryUsageRate;

    private String areaid;

    private String ip;

    private Integer port;

    private String url;

    private String checkSn;

    private Integer logicDelete;

    private String companyId;

    private String time;

    private String psign;

    private String tempAreaid;

    private Integer groupNo;

    private Byte imgNum;

    private Integer adIntervaltime;

    private String installTime;

    private Integer maintenancePeriod;
    /**
     *自助终端配置的单位数量
     **/
    private Integer companyCount;
    /**
     * 用于显示的地区
     */
    private String areaName;
    /**
     * 类型名称
     */
    private String typeName;
    /**
     * 真正的地区
     */
    private String realAreaName;
    /**
     * 真正的区域标识
     */
    private String realAreaid;
}