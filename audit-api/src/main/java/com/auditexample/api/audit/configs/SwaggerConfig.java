package com.auditexample.api.audit.configs;

import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

import com.auditexample.api.audit.controllers.AuditEventController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2019-09-13<br/>
 */

@Configuration
@EnableSwagger2
@ConditionalOnProperty(
    value="api.docs.internal",
    havingValue = "true")
public class SwaggerConfig {

  @Bean
  public Docket api() {
    return new Docket(SWAGGER_2)
        .apiInfo(apiInfo())
        .groupName("internal")
        .useDefaultResponseMessages(false)
        .forCodeGeneration(true)
        .ignoredParameterTypes(Authentication.class)
        .select()
        .apis(RequestHandlerSelectors.basePackage(AuditEventController.class.getPackage().getName()))
        .paths(PathSelectors.any())
        .build();
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("Auditexample Audit")
        .description("Audit Service")
        .contact(new Contact("Auditexample", "https://www.auditexample.com/", ""))
        .version("2.0")
        .build();
  }

}
