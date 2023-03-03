package com.demo.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
   @Bean
   Docket api() {
      return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiInfo());
   }

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo("Client REST API", "Demo Client REST API", 
              "1.0.0", 
              "", 
              new Contact("Dewald Pretorius", "https://www.linkedin.com/in/dewald-pretorius-32363721b/", "pretorius.dewald@gmail.com"), 
              "Apache 2.0", 
              "http://www.apache.org/licenses/LICENSE-2.0.html", 
              Collections.emptyList());
        return apiInfo;
    }
}