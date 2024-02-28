package com.czc.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static ApiInfo DEFAULT = null;
    @Bean
    public Docket docket(){

        DEFAULT = new ApiInfo("橙草云接口","","","","","","");
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(DEFAULT)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.czc.Controller"))//按照包名扫描
                .build();
    }
}
