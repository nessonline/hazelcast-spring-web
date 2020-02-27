package ru.parma.ventslavovich.hazelcast;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import ru.parma.ventslavovich.hazelcast.config.ApplicationConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ApplicationConfig.class)
@TestPropertySource(properties = "hazelcast.declaration-map.init=false")
@SpringBootTest
public @interface HazelcastSpringWebApplicationTest {
}
