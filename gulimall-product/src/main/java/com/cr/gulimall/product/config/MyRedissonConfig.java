package com.cr.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author cr
 * @date 2022/3/29 22:33
 */
@Configuration
public class MyRedissonConfig {
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        // 1、创建配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");

        // 2、根据Config创建出RedissonClient实例
        return Redisson.create(config);
    }
}
