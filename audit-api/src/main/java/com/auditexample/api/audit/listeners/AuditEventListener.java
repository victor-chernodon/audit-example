package com.auditexample.api.audit.listeners;

import com.auditexample.api.audit.common.domain.vo.AuditEventVO;
import com.auditexample.api.audit.common.services.AuditEventLogService;
import com.auditexample.api.audit.properties.KafkaTopic;
import com.auditexample.api.common.security.client.permissions.Resources;
import com.auditexample.api.common.security.client.permissions.Roles;
import com.auditexample.api.common.security.client.permissions.Scopes;
import com.auditexample.api.common.security.resource.annotation.AuthorizeByOauthToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuditEventListener {

  @Autowired
  private KafkaTopic kafkaTopic;

  @Autowired
  private AuditEventLogService auditEventLogService;

  @KafkaListener(
      topics = "#{kafkaTopic.getTopicName()}",
      groupId = "#{kafkaTopic.getGroupId()}",
      containerFactory = "kafkaListenerContainerFactory")
  @AuthorizeByOauthToken(tokenValue = "[1]", role = Roles.CLIENT, resource = Resources.AUDIT_LOGS, scope = Scopes.WRITE)
  public void onMessage(@Payload AuditEventVO auditEventVO, @Header("Authorization") String tokenValue) {
    try {
      auditEventLogService.logEvent(auditEventVO);
      log.debug("Processed and saved - {}", auditEventVO);
    } catch (Exception e) {
      log.error("Exception while processing audit event! {}", auditEventVO, e);
    }
  }
}
