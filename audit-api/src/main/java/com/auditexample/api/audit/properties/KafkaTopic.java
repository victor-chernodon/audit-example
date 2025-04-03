package com.auditexample.api.audit.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2019-09-12<br/>
 */
@Component
@ConfigurationProperties(prefix = "kafka.topic")
@Getter
@Setter
public class KafkaTopic {

  @Autowired
  private Integer contextShardId;

  @Value("${spring.profiles.active:}")
  private String applicationProfile;

  private String groupId;
  private String prefix;
  private Short replicationFactor;
  private Integer partitionCount;

  public String getTopicName() {
    return prefix + "-" + contextShardId + "-" + applicationProfile;
  }

}
