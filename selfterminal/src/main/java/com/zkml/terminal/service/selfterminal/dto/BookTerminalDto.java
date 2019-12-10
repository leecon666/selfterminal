package com.zkml.terminal.service.selfterminal.dto;

import lombok.Data;

import java.util.Date;

@Data
public class BookTerminalDto {
    private Integer id;
    /**
     * 多个单位
     */
    private String[] moreCompany;
    /**
     * 终端号
     */
    private String sn;
    /**
     * 区域ID
     */
    private String areaid;
    /**
     * 逻辑删除
     */
    private Integer logicDelete = 0;
    /**
     * 命令  10:终端参数查询  11:终端升级  12:终端重启
     */
    private Integer command;
    /**
     * 模糊地区
     */
    private String areaIdLike;
    /**
     * 终端类型
     */
    private String type;

    private String version;
    /**
     * 状态(1:离线,0:在线)
     */
    private Integer status;

    private Date createTime;

    private Date updateTime;

    private String ip;

    private Integer port;

    private String url;

    private String checkSn;

    private String companyId;

    private String time;

    private String psign;

    private String tempAreaid;

    private Integer groupNo;

    private Byte imgNum;

    private Integer adIntervaltime;

    private Date installTime;

    private Integer maintenancePeriod;
    /**
     * 原智能柜组数
     */
    private Integer oldGroupNo;
    /**
     * 真正的区域标识
     */
    private String realAreaid;
}
