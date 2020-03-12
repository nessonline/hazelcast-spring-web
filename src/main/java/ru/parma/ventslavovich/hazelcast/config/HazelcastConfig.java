package ru.parma.ventslavovich.hazelcast.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

public class HazelcastConfig {

    public static final String MAP_SEARCH_DECLARATION_NAME = "declaration-map";

    @Bean("hazelcastInstance")
    @Profile("server")
    HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.getGroupConfig().setName("dev");
        config.getNetworkConfig().setPort(5701);
        MapConfig mapConfig = config.getMapConfig(MAP_SEARCH_DECLARATION_NAME);
        mapConfig.setInMemoryFormat(InMemoryFormat.OBJECT);
        return Hazelcast.newHazelcastInstance(config);
    }

    @Bean
    @Profile("client")
    ClientConfig clientConfig() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getUserCodeDeploymentConfig().setEnabled(true);
        clientConfig.getGroupConfig().setName("dev");
        clientConfig.getNetworkConfig().addAddress("10.50.10.134", "10.50.10.134:5702");
        return clientConfig;
    }

    @Bean("hazelcastInstance")
    @Profile("client")
    HazelcastInstance hazelcastInstance(ClientConfig clientConfig) {
        return HazelcastClient.newHazelcastClient(clientConfig);
    }
}
