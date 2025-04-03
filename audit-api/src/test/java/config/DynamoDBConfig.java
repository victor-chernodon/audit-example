package config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.auditexample.api.audit.configs.CommonDynamoDBConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Created at Auditexample<br/> User: Vladimir Gromov<br/> Date: 2020-04-20<br/>
 */
@TestConfiguration
@Import(CommonDynamoDBConfig.class)
@ComponentScan(basePackages = "com.auditexample.api.audit.db.repositories")
public class DynamoDBConfig {

  @Bean
  public AmazonDynamoDB amazonDynamoDB() {
    return DynamoDBEmbedded.create().amazonDynamoDB();
  }

  @Bean
  public DynamoDB dynamoDB(AmazonDynamoDB amazonDynamoDB) {
    return new DynamoDB(amazonDynamoDB);
  }

}
