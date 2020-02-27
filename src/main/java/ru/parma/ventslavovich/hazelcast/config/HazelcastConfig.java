package ru.parma.ventslavovich.hazelcast.config;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;

public class HazelcastConfig {

    @Bean
    HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.getGroupConfig().setName("dev");
        config.getNetworkConfig().setPort(5701);
        return Hazelcast.newHazelcastInstance(config);
    }

    /*@Bean
    ClientConfig clientConfig() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getGroupConfig().setName("dev");
        clientConfig.getNetworkConfig().addAddress("10.1.5.167");
        return clientConfig;
    }

    @Bean
    @Primary
    HazelcastInstance hazelcastInstance(ClientConfig clientConfig) {
        return HazelcastClient.newHazelcastClient(clientConfig);
    }*/
}
