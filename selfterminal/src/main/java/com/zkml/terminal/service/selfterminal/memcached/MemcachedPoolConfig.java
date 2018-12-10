package com.zkml.terminal.service.selfterminal.memcached;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: likun
 * @Date: Created in  2018/12/10 16:00
 * @Description:
 */
@Data
@Component
@ConfigurationProperties(prefix = "memcached")
public class MemcachedPoolConfig {
    private String[] servers;
    private Integer[] weights;
    private int initConn;
    private Integer minConn;
    private Integer maxConn;
    private Long maintSleep;
    private boolean nagle;
    private Integer socketTo;
}
