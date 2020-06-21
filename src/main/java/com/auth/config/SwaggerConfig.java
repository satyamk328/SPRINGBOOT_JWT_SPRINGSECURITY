package com.auth.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * The Class SwaggerConfig.
 * 
 * Generate Swagger UI.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * filtering controllers to expose only some urls
     * 
     * @return the docket
     */
    @Bean
    public Docket api() {
        ApiSelectorBuilder apiBuilder = 
                new Docket(DocumentationType.SWAGGER_2)
                    .select()
                    .apis(RequestHandlerSelectors.any());
        apiBuilder.paths(PathSelectors.regex("/.*"));
        
        Parameter authorizationHeader = 
                new ParameterBuilder()
                    .name("Authorization")
                    .description("For accessing the API a valid JWT token must be passed in all the queries "
                            + "in the 'Authorization' header using the following format. Bearer: JWTToken")
                    .modelRef(new ModelRef("string"))
                    .parameterType("header")
                    .required(false)
                    .build();

       List<Parameter> param = new ArrayList<>();
       param.add(authorizationHeader);

       Parameter langParam = 
               new ParameterBuilder()
                   .name("Language")
                   .description("Language param")
                   .modelRef(new ModelRef("string"))
                   .parameterType("query")
                   .required(false)
                   .build();
       param.add(langParam);

       
       
       return apiBuilder.build().globalOperationParameters(param);
    }
}