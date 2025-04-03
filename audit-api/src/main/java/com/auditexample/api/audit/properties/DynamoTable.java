package com.auditexample.api.audit.properties;

import static com.auditexample.api.audit.db.DBMetaData.SHARD_PREFIX;
import static com.auditexample.api.audit.db.DBMetaData.UNDERSCORE;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2019-09-12<br/>
 */
@Component
@ConfigurationProperties(prefix = "dynamo.table")
@Getter
@Setter
public class DynamoTable {

  @Autowired
  private Integer contextShardId;

  private String suffix;

  public String getTableNameSuffix() {
    return UNDERSCORE +
        SHARD_PREFIX +
        UNDERSCORE +
        contextShardId +
        UNDERSCORE +
        suffix;
  }

}
