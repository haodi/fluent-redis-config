package com.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;

@Component
public class RedisConfig implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {
    private static Logger logger = LoggerFactory.getLogger(RedisConfig.class);
    private Map<String, Object> redisConfigMap;

    @Value("${maxTotal:1000}")
    private int maxTotal;

    @Value("${maxIdle:500}")
    private int maxIdle;

    private JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setTestOnBorrow(false);
        jedisPoolConfig.setTestOnReturn(false);
        jedisPoolConfig.setTestWhileIdle(true);
        return jedisPoolConfig;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        redisConfigMap.forEach((beanName, host) -> {
            if (beanName == null || beanName.isEmpty()) {
                throw new NullPointerException("initialize redis configuration failed because " +
                        "redis name can not be empty.");
            }
            if (host == null) {
                throw new NullPointerException("initialize redis configuration failed because " +
                        "host can not be empty.");
            }
            logger.info("initialize redis({}) config ...", beanName);

            String ip = ((String) host).split(":")[0];
            int port = Integer.parseInt(((String) host).split(":")[1]);
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(JedisPool.class);
            beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, this.jedisPoolConfig());
            beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(1, ip);
            beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(2, port);
            beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(3, 5000); //timeout为5秒
            beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
        });
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory)
            throws BeansException {
    }

    @Override
    public void setEnvironment(Environment environment) {
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, "redis.");
        redisConfigMap = propertyResolver.getSubProperties("config-set.");
    }
}