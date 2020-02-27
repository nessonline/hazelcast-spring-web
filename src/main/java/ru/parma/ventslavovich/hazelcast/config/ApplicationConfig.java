package ru.parma.ventslavovich.hazelcast.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import({
        HazelcastConfig.class,
        SecurityConfig.class,
        SwaggerConfig.class
})
@ComponentScan(basePackages = {"ru.parma.ventslavovich.hazelcast"})
public class ApplicationConfig {
}
