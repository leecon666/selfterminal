package com.zkml.terminal.service.selfterminal.server.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: likun
 * @Date: Created in  2018/12/18 9:41
 * @Description:
 */
@Data
@Component
@ConfigurationProperties(prefix = "netty")
public class NettyConfig {
    /**
     * tcp协议端口
     */
    private Integer tcpPort;
    /**
     * bossGroup线程
     */
    private Integer bossThread;
    /**
     * workerGroup线程
     */
    private Integer workerThread;
    /**
     * 保持连接
     */
    private boolean keepAlive;
    /**
     * 保持连接数
     */
    private Integer backLog;
    /**
     * 有数据立即发送
     */
    private boolean nodelay;
}
