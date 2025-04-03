package com.auditexample.api.audit.db.state;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.auditexample.api.audit.db.migration.AuditEventsChangelog;
import com.auditexample.api.audit.db.utils.DynamoDBUtils;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2020-04-08<br/>
 */

@Slf4j
@AllArgsConstructor
public class DynamoDBStateManager {

  private AmazonDynamoDB amazonDynamoDB;
  private String tableName;

  @PostConstruct
  public void onConstruct() {
    log.debug("onConstruct method");
    AuditEventsChangelog.auditEventTableCreate(amazonDynamoDB, tableName);
    DynamoDBUtils.listTables(amazonDynamoDB.listTables(), this.getClass().getSimpleName());
  }

  @PreDestroy
  public void onDestroy() {
    log.debug("onDestroy method");
    if (amazonDynamoDB != null) {
      log.debug("dynamoDbLocal shutdown");
      amazonDynamoDB.shutdown();
    }
  }

}
