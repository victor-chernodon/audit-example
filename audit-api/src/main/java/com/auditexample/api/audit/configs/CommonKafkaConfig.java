package com.auditexample.api.audit.configs;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.auditexample.api.audit.common.domain.vo.AuditEventVO;
import com.auditexample.api.audit.properties.KafkaService;
import com.auditexample.api.audit.properties.KafkaTopic;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@EnableKafka
public class CommonKafkaConfig {

  @Autowired
  private KafkaService kafkaService;

  @Autowired
  private KafkaTopic kafkaTopic;

  @Bean
  public ConsumerFactory<String, AuditEventVO> kafkaConsumerFactory() {
    JsonDeserializer<AuditEventVO> deserializer = new JsonDeserializer<>(AuditEventVO.class, objectMapper());
    deserializer.setRemoveTypeHeaders(false);
    deserializer.addTrustedPackages("*");
    deserializer.setUseTypeMapperForKey(true);
    return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(), deserializer);
  }

  @Bean
  public Map<String, Object> consumerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaService.getBootstrapServers());
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    return props;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, AuditEventVO> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, AuditEventVO> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(kafkaConsumerFactory());
    return factory;
  }

  @Bean
  public KafkaAdmin admin() {
    Map<String, Object> props = new HashMap<>();
    props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaService.getBootstrapServers());
    return new KafkaAdmin(props);
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
  public NewTopic auditEventsTopic() {
    return new NewTopic(
        kafkaTopic.getTopicName(),
        kafkaTopic.getPartitionCount(),
        kafkaTopic.getReplicationFactor());
  }

}
