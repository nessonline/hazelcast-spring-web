package ru.parma.ventslavovich.hazelcast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.parma.ventslavovich.hazelcast.config.ApplicationConfig;

@SpringBootApplication
@Import(ApplicationConfig.class)
public class HazelcastSpringWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(HazelcastSpringWebApplication.class, args);
    }

}
