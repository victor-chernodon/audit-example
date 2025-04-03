package com.auditexample.api.audit.configs;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.auditexample.api.audit.properties.AmazonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2020-04-07<br/>
 */

@Configuration
@Profile("local")
@Import(CommonDynamoDBConfig.class)
@Slf4j
public class LocalDynamoDBConfig {

  @Autowired
  private AmazonService amazonService;

  @Value("${dynamo.url:}")
  private String dynamoUrl;

  @Bean
  public AmazonDynamoDB amazonDynamoDB() {
    var region = Regions.fromName(amazonService.getRegion());
    log.debug("AWS region resolved: {}", region);

    AmazonDynamoDBClientBuilder amazonDynamoDBClientBuilder =
        AmazonDynamoDBClientBuilder.standard()
            .withCredentials(
                new AWSStaticCredentialsProvider(amazonAWSCredentials()));

    if (StringUtils.isEmpty(dynamoUrl)) {
      amazonDynamoDBClientBuilder.withRegion(region);
    } else {
      amazonDynamoDBClientBuilder.withEndpointConfiguration(
          // we can use any region here
          new AwsClientBuilder.EndpointConfiguration(dynamoUrl, region.getName()));
    }
    return amazonDynamoDBClientBuilder.build();
  }

  private AWSCredentials amazonAWSCredentials() {
    return new BasicAWSCredentials(amazonService.getAccesskey(), amazonService.getSecretkey());
  }

}
