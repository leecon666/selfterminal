package com.zkml.terminal.service.selfterminal;

import com.zkml.terminal.service.selfterminal.server.nettyserver.NettyServer;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @Author: likun
 * @Date: Created in  2018/12/18 9:31
 * @Description:nettyserver启动类
 */
@SpringBootApplication
@MapperScan("com.zkml.terminal.service.selfterminal.dao")
@Slf4j
public class ZKMLApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ZKMLApplication.class);
        ConfigurableApplicationContext ctx = application.run(args);
        NettyServer nettyServer = ctx.getBean(NettyServer.class);
        try {
            log.info("自助终端服务启动成功");
            nettyServer.startNettyServer();
        } catch (Exception e) {
            log.error("自助终端服务启动失败,",e.getMessage());
        }
    }
}
