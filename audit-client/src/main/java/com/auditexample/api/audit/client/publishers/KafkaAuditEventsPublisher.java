package com.auditexample.api.audit.client.publishers;

import com.auditexample.api.audit.client.domain.vo.AuditEventVO;
import com.auditexample.api.audit.client.security.token.TokenConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
public class KafkaAuditEventsPublisher implements AuditEventsPublisher {

  private final String topic;
  private final KafkaTemplate<String, AuditEventVO> kafkaTemplate;

  public KafkaAuditEventsPublisher(String topic, KafkaTemplate<String, AuditEventVO> kafkaTemplate) {
    this.topic = topic;
    this.kafkaTemplate = kafkaTemplate;
  }

  @Async
  public void publish(AuditEventVO auditEventMessage, String tokenValue) {
    try {
      Message<AuditEventVO> message = MessageBuilder
          .withPayload(auditEventMessage)
          .setHeader(KafkaHeaders.TOPIC, topic)
          .setHeader(TokenConstants.AUTHORIZATION, TokenConstants.BEARER + tokenValue)
          .build();
      ListenableFuture<SendResult<String, AuditEventVO>> future = kafkaTemplate.send(message);
      future.addCallback(
          new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, AuditEventVO> result) {
              log.debug(
                  "Sent message= "
                      + auditEventMessage
                      + " with offset= "
                      + result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable ex) {
              log.error("Could not publish event to topic", ex);
            }
          });
    } catch (Exception ex) {
      log.error("Could not send audit message", ex);
    }
  }
}
