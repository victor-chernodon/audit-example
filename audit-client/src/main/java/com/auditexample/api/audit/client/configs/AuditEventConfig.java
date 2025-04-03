package com.auditexample.api.audit.client.configs;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
@Import(KafkaConfig.class)
@ComponentScan({
  "com.auditexample.api.audit.client"
})
@ConditionalOnExpression("${audit.enable:false} && !'${audit.auth.clientId:}'.isEmpty()")
public class AuditEventConfig {

}
