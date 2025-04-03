package com.auditexample.api.audit.db.health;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2020-04-21<br/>
 */

@Component
@AllArgsConstructor
public class DynamoDBHealthIndicator implements HealthIndicator {

  private final AmazonDynamoDB amazonDynamoDB;
  private final String auditEventsTableName;

  @Override
  public Health health() {

    try {
      TableDescription tableDescription = amazonDynamoDB.describeTable(auditEventsTableName).getTable();
      // If table is not active - service unavailable
      if (!TableStatus.ACTIVE.toString().equals(tableDescription.getTableStatus())) {
        return Health.outOfService().withDetail("Table is not active", auditEventsTableName).build();
      }
    } catch (ResourceNotFoundException e) {
      return Health.down().withException(e).withDetail("Table do not exist", auditEventsTableName).build();
    } catch (AmazonDynamoDBException ie) {
      return Health.down().withException(ie)
          .withDetail("An error occurred on the server side during the table lookup", auditEventsTableName).build();
    } catch (Exception ex) {
      return Health.down().withException(ex)
          .withDetail("An error occurred during the table lookup", auditEventsTableName).build();
    }
    return Health.up().build();
  }

}
