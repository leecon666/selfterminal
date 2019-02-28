package com.zkml.terminal.service.selfterminal.model;

import lombok.Data;

import java.util.Date;

/**
 * @Author: likun
 * @Date: Created in  2019/2/25 13:58
 * @Description:人工智能档案管理终端实体类
 */
@Data
public class ProfileTerminal {
    /**
     * 主键
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
     * 状态(1:离线,0:在线)
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
     * 用于显示的地区
     */
    private String areaName;
    /**
     * 类型名称
     */
    private String typeName;
    /**
     * 单位ID
     */
    private String companyId;
    /**
     * 库标识
     */
    private String psign;
    /**
     * 单位名称
     */
    private String companyName;
    /**
     * 真正的地区
     */
    private String realAreaName;
    /**
     * 真正的区域标识
     */
    private String realAreaid;
}