package com.auditexample.api.audit.db.utils;

import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import lombok.extern.slf4j.Slf4j;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2020-04-15<br/>
 */

@Slf4j
public class DynamoDBUtils {

  public static void listTables(ListTablesResult result, String method) {
    log.debug("found {} tables with {}", result.getTableNames().size(), method);
    for (String table : result.getTableNames()) {
      log.debug("table: {}", table);
    }
  }

}
