package com.zkml.terminal.service.selfterminal.memcached;

import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Author: likun
 * @Date: Created in  2018/12/10 15:52
 * @Description:memcache缓存配置
 */
@Component
public class MemcacheConfig {
    @Autowired
    private MemcachedPoolConfig memcachedPoolConfig;

    @Bean(name = "sockIOPool")
    public SockIOPool sockIOPool() {
        /** 获取连接池的实例 */
        SockIOPool pool = SockIOPool.getInstance();
        String[] servers = memcachedPoolConfig.getServers();
        Integer[] weights = memcachedPoolConfig.getWeights();
        pool.setServers(servers);
        pool.setWeights(weights);
        pool.setInitConn(memcachedPoolConfig.getInitConn());
        pool.setMinConn(memcachedPoolConfig.getMinConn());
        pool.setMaxConn(memcachedPoolConfig.getMaxConn());
        pool.setMaintSleep(memcachedPoolConfig.getMaintSleep());
        pool.setNagle(memcachedPoolConfig.isNagle());
        pool.setSocketConnectTO(memcachedPoolConfig.getSocketTo());
        /** 初始化连接池并启动 */
        pool.initialize();
        pool.setFailover(true);
        pool.setAliveCheck(true);
        return pool;
    }

    @Bean(name = "memCachedClient")
    public MemCachedClient memCachedClient() {
        return new MemCachedClient();
    }
}
