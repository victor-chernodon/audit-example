package com.auditexample.api.audit.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.requestMatchers()
        .and()
        .authorizeRequests()
        .antMatchers("/__manage/**", "/version").permitAll()
        .antMatchers("/v2/api-docs", "/swagger-resources/configuration/ui", "/swagger-resources",
            "/swagger-resources/configuration/security", "/swagger-ui.html", "/webjars/**", "/csrf")
        .permitAll()
        .anyRequest().authenticated()
        .and()
        .csrf().disable();
  }

}

