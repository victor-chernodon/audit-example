package com.auditexample.api.audit.client.configs;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orbitz.consul.Consul;
import com.auditexample.api.audit.client.domain.vo.AuditEventVO;
import com.auditexample.api.audit.client.publishers.KafkaAuditEventsPublisher;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.StringUtils;

@Configuration
@Slf4j
public class KafkaConfig {

  @Value("${consul.host:127.0.0.1}")
  private String host;

  @Value("${consul.port:8500}")
  private int port;

  @Autowired(required = false)
  private Consul consulClient;

  @Value("${kafka.bootstrap-servers:}")
  private String bootstrapServers;

  @Value("${kafka.consul.service:}")
  private String consulServiceKey;

  @Value("${kafka.consul.tag:}")
  private String consulServiceTag;

  @Value("${kafka.buffer.memory:131072}")
  private int bufferMemory;

  @Value("${kafka.max.block.ms:15000}")
  private int maxBlockMs;

  @Value("${audit.topic.name}")
  private String auditEventsTopic;

  @Bean
  public ProducerFactory<String, AuditEventVO> producerFactory() {
    return new DefaultKafkaProducerFactory<>(producerConfigs());
  }

  @Bean
  public KafkaAuditEventsPublisher auditEventsPublisher(KafkaTemplate<String, AuditEventVO> kafkaTemplate) {
    return new KafkaAuditEventsPublisher(auditEventsTopic, kafkaTemplate);
  }

  private ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    return objectMapper;
  }

  @Bean
  public KafkaTemplate<String, AuditEventVO> kafkaTemplate(
      ProducerFactory<String, AuditEventVO> kafkaProducerFactory) {
    return new KafkaTemplate<>(kafkaProducerFactory);
  }

  private Map<String, Object> producerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers());
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
    props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, maxBlockMs);
    return props;
  }

  private String getBootstrapServers() {
    if (consulClient == null || StringUtils.isEmpty(consulServiceKey)) {
      return bootstrapServers;
    }

    String healthyServiceInstances = consulClient
        .healthClient()
        .getHealthyServiceInstances(consulServiceKey)
        .getResponse()
        .stream()
        .filter(instance -> StringUtils.isEmpty(consulServiceTag) || instance.getService().getTags()
            .contains(consulServiceTag))
        .map(instance -> org.apache.commons.lang3.StringUtils.defaultIfEmpty(
            instance.getService().getAddress(), instance.getNode().getAddress()) + ":" + instance
            .getService().getPort())
        .collect(Collectors.joining(","));

    if (StringUtils.isEmpty(healthyServiceInstances)) {
      return bootstrapServers;
    }

    return healthyServiceInstances;
  }
}