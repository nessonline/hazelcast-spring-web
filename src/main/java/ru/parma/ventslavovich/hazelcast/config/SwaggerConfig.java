package ru.parma.ventslavovich.hazelcast.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
public class SwaggerConfig {

    public static final String API_SEARCH_DECLARATION_CONTROLLER_NAME = "api-search-declaration-controller";

    @Value("${spring.application.name}")
    private String appName;
    @Value("${spring.application.description}")
    private String appDescription;
    @Value("${spring.application.version}")
    private String appVersion;

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("rest-api")
                .select()
                .paths(PathSelectors.ant("/api/**"))
                .build()
                .apiInfo(apiInfo())
                .tags(new Tag(API_SEARCH_DECLARATION_CONTROLLER_NAME, "Контроллер витрины деклараций соответствия"))
                ;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title(appName).description(appDescription).version(appVersion).build();
    }

}
