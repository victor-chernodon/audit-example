package com.auditexample.api.audit.configs;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameResolver;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.auditexample.api.audit.properties.DynamoTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class CommonDynamoDBConfig {

  @Autowired
  private DynamoTable dynamoTable;

  @Bean
  public DynamoDBMapperConfig dynamoDBMapperConfig(TableNameResolver tableNameResolver) {
    DynamoDBMapperConfig.Builder builder = new DynamoDBMapperConfig.Builder();
    builder.setTableNameResolver(tableNameResolver);
    builder.setTypeConverterFactory(DynamoDBMapperConfig.DEFAULT.getTypeConverterFactory());
    return builder.build();
  }

  @Bean
  public DynamoDBMapperConfig.TableNameResolver tableNameResolver() {
    return (clazz, config) -> {
      final DynamoDBTable dynamoDBTable = clazz.getDeclaredAnnotation(DynamoDBTable.class);
      if (dynamoDBTable == null) {
        throw new DynamoDBMappingException(clazz + " not annotated with @DynamoDBTable");
      }
      log.debug("Table name suffix: {}, shard id: {}", dynamoTable.getSuffix(), dynamoTable.getContextShardId());
      var tableName = dynamoDBTable.tableName() + dynamoTable.getTableNameSuffix();
      log.debug("Table name: {}", tableName);
      return tableName;
    };
  }

  @Bean
  public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB) {
    return new DynamoDBMapper(amazonDynamoDB);
  }

}
