package ru.parma.ventslavovich.hazelcast.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import ru.parma.ventslavovich.hazelcast.data.entity.SearchDeclaration;
import ru.parma.ventslavovich.hazelcast.data.filter.SearchDeclarationComparator;

//@EnableAsync
public class HazelcastConfig {

    @Bean
    @Profile("server")
    HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.getGroupConfig().setName("dev");
        config.getNetworkConfig().setPort(5701);
        return Hazelcast.newHazelcastInstance(config);
    }

    @Bean
    @Profile("client")
    ClientConfig clientConfig() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getUserCodeDeploymentConfig().setEnabled(true);
        clientConfig.getUserCodeDeploymentConfig().addClass(SearchDeclaration.class);
        clientConfig.getUserCodeDeploymentConfig().addClass(SearchDeclarationComparator.class);
        clientConfig.getGroupConfig().setName("dev");
        clientConfig.getNetworkConfig().addAddress("10.50.10.134", "10.50.10.134:5702");
        return clientConfig;
    }

    @Bean
    @Profile("client")
    @Primary
    HazelcastInstance hazelcastInstance(ClientConfig clientConfig) {
        return HazelcastClient.newHazelcastClient(clientConfig);
    }
}
