package com.auditexample.api.audit.configs;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2020-04-07<br/>
 */

@Configuration
@Profile("!local")
@Import(CommonDynamoDBConfig.class)
@Slf4j
public class DynamoDBConfig {

  @Value("${AWS_REGION:eu-central-1}")
  private String amazonAWSRegion;

  public AWSCredentialsProvider amazonAWSCredentialsProvider() {
    return InstanceProfileCredentialsProvider.getInstance();
  }

  @Bean
  public AmazonDynamoDB amazonDynamoDB() {
    var region = Regions.fromName(amazonAWSRegion);
    log.debug("AWS region resolved: {}", region);
    return AmazonDynamoDBClientBuilder.standard()
        .withCredentials(amazonAWSCredentialsProvider())
        .withRegion(region)
        .build();
  }

}
