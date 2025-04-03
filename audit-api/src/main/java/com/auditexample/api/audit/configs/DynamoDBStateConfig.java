package com.auditexample.api.audit.configs;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.auditexample.api.audit.db.entities.AuditEvent;
import com.auditexample.api.audit.db.state.DynamoDBStateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2020-04-07<br/>
 */

@Configuration
public class DynamoDBStateConfig {

  @Bean
  public String auditEventsTableName(DynamoDBMapperConfig dynamoDBMapperConfig) {
    return dynamoDBMapperConfig.getTableNameResolver().getTableName(AuditEvent.class, dynamoDBMapperConfig);
  }

  @Bean
  @Autowired
  @ConditionalOnProperty(
      value="dynamo.state.manager.enable",
      havingValue = "true")
  public DynamoDBStateManager dynamoDBStateManager(AmazonDynamoDB amazonDynamoDB, DynamoDBMapperConfig dynamoDBMapperConfig) {
    return new DynamoDBStateManager(amazonDynamoDB, auditEventsTableName(dynamoDBMapperConfig));
  }
}
