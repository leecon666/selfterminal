package com.zkml.terminal.service.selfterminal.model;

import lombok.Data;

import java.util.Date;

/**
 * @Author: likun
 * @Date: Created in  2018/12/7 16:52
 * @Description:自助终端实体类
 */
@Data
public class SelfTerminal {
    /**
     * self_terminal_new表主键
     */
    private Integer id;
    /**
     * 终端号
     */
    private String sn;
    /**
     * 终端类型
     */
    private String type;
    /**
     * 版本
     */
    private String version;
    /**
     * 状态(离线,在线)
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 上传时间
     */
    private String time;
    /**
     * CPU使用率
     */
    private String cpuUsageRate;
    /**
     * 内存使用率
     */
    private String memoryUsageRate;
    /**
     * 区域标识
     */
    private String areaid;
    /**
     * TCP请求IP
     */
    private String ip;
    /**
     * TCP请求端口号
     */
    private Integer port;
    /**
     * Web服务请求地址
     */
    private String url;
    /**
     * 检测终端序列号
     */
    private String checkSn;
    /**
     * 删除标识， 0 可用  1 不可用
     */
    private Integer logicDelete;
    /**
     * 地区
     */
    private String areaName;
    /**
     * 类型名称
     */
    private String typeName;
    /**
     * 单位ID
     */
    private Integer companyId;
}
