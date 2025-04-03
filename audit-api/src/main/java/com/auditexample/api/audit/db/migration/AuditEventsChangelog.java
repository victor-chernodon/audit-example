package com.auditexample.api.audit.db.migration;

import static com.auditexample.api.audit.db.DBMetaData.AUDIT_EVENTS_GSI_OBJECT_INDEX_NAME;
import static com.auditexample.api.audit.db.DBMetaData.AUDIT_EVENTS_GSI_PARENT_REF_INDEX_NAME;
import static com.auditexample.api.audit.db.DBMetaData.AUDIT_EVENTS_HASH_KEY_NAME;
import static com.auditexample.api.audit.db.DBMetaData.AUDIT_EVENTS_OBJECT_ID_HASH_KEY_NAME;
import static com.auditexample.api.audit.db.DBMetaData.AUDIT_EVENTS_PARENT_OBJECT_REFERENCE_ID_HASH_KEY_NAME;
import static com.auditexample.api.audit.db.DBMetaData.AUDIT_EVENTS_RANGE_KEY_NAME;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2020-04-15<br/>
 */

@Slf4j
public class AuditEventsChangelog {

  public static void auditEventTableCreate(AmazonDynamoDB amazonDynamoDB, String tableName) {
    log.info("Searching for an existing {} table...", tableName);

    try {
      TableDescription tableDescription = amazonDynamoDB.describeTable(tableName).getTable();
      log.info("Table found with status: {}", tableDescription.getTableStatus());
    } catch (ResourceNotFoundException e) {
      log.info("Attempting to create {} table; please wait...", tableName);

      List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
      attributeDefinitions.add(new AttributeDefinition(AUDIT_EVENTS_HASH_KEY_NAME, ScalarAttributeType.S));
      attributeDefinitions.add(new AttributeDefinition(AUDIT_EVENTS_RANGE_KEY_NAME, ScalarAttributeType.N));
      attributeDefinitions.add(new AttributeDefinition(AUDIT_EVENTS_OBJECT_ID_HASH_KEY_NAME, ScalarAttributeType.S));
      attributeDefinitions.add(new AttributeDefinition(
          AUDIT_EVENTS_PARENT_OBJECT_REFERENCE_ID_HASH_KEY_NAME, ScalarAttributeType.S));

      List<KeySchemaElement> tableKeySchema = new ArrayList<>();
      tableKeySchema.add(new KeySchemaElement(AUDIT_EVENTS_HASH_KEY_NAME, KeyType.HASH));
      tableKeySchema.add(new KeySchemaElement(AUDIT_EVENTS_RANGE_KEY_NAME, KeyType.RANGE));

      ProvisionedThroughput provisionedthroughput = new ProvisionedThroughput(1000L, 1000L);

      GlobalSecondaryIndex objectIdGsi = new GlobalSecondaryIndex()
          .withIndexName(AUDIT_EVENTS_GSI_OBJECT_INDEX_NAME)
          .withProvisionedThroughput(new ProvisionedThroughput()
              .withReadCapacityUnits(1000L)
              .withWriteCapacityUnits(1000L))
          .withProjection(new Projection().withProjectionType(ProjectionType.ALL));

      List<KeySchemaElement> objectIdGsiKeySchema = new ArrayList<>();
      objectIdGsiKeySchema.add(new KeySchemaElement(AUDIT_EVENTS_OBJECT_ID_HASH_KEY_NAME, KeyType.HASH));
      objectIdGsiKeySchema.add(new KeySchemaElement(AUDIT_EVENTS_RANGE_KEY_NAME, KeyType.RANGE));
      objectIdGsi.setKeySchema(objectIdGsiKeySchema);

      GlobalSecondaryIndex parentRefIdGsi = new GlobalSecondaryIndex()
          .withIndexName(AUDIT_EVENTS_GSI_PARENT_REF_INDEX_NAME)
          .withProvisionedThroughput(new ProvisionedThroughput()
              .withReadCapacityUnits(1000L)
              .withWriteCapacityUnits(1000L))
          .withProjection(new Projection().withProjectionType(ProjectionType.ALL));

      List<KeySchemaElement> parentRefIdGsiKeySchema = new ArrayList<>();
      parentRefIdGsiKeySchema.add(new KeySchemaElement(AUDIT_EVENTS_PARENT_OBJECT_REFERENCE_ID_HASH_KEY_NAME, KeyType.HASH));
      parentRefIdGsiKeySchema.add(new KeySchemaElement(AUDIT_EVENTS_RANGE_KEY_NAME, KeyType.RANGE));
      parentRefIdGsi.setKeySchema(parentRefIdGsiKeySchema);

      CreateTableRequest request =
          new CreateTableRequest()
              .withTableName(tableName)
              .withAttributeDefinitions(attributeDefinitions)
              .withKeySchema(tableKeySchema)
              .withGlobalSecondaryIndexes(objectIdGsi, parentRefIdGsi)
              .withProvisionedThroughput(provisionedthroughput);

      try {
        CreateTableResult result = amazonDynamoDB.createTable(request);
        log.info("Table {} created, status: {}", tableName, result.getTableDescription().getTableStatus());
      } catch (final ResourceInUseException re) {
        if (log.isTraceEnabled()) {
          log.trace("Table {} already exists", tableName);
        }
      } catch (Exception ex) {
        log.error("Table creation error", ex);
      }
    }
  }


}
