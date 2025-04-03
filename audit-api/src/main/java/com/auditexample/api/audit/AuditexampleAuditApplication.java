package com.auditexample.api.audit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.auditexample.api")
public class AuditexampleAuditApplication {

  public static void main(String[] args) {
    SpringApplication.run(AuditexampleAuditApplication.class, args);
  }

}
