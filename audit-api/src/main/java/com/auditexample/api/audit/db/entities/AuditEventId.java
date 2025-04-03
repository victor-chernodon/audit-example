package com.auditexample.api.audit.db.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.auditexample.api.audit.db.DBMetaData;
import java.util.Random;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Setter
@ToString
@EqualsAndHashCode
public class AuditEventId {

  private String customerIdWithSuffix;
  private Long timestampUTC;

  public AuditEventId(int customerId, Long timestampUTC) {
    this.customerIdWithSuffix = buildKey(customerId);
    this.timestampUTC = timestampUTC;
  }

  public AuditEventId(int customerId, int suffix) {
    this.customerIdWithSuffix = customerId + DBMetaData.AUDIT_EVENTS_HASH_KEY_SEPARATOR + suffix;
  }

  @DynamoDBHashKey(attributeName = DBMetaData.AUDIT_EVENTS_HASH_KEY_NAME)
  public String getAuditEventKey() {
    return customerIdWithSuffix;
  }

  public void setAuditEventKey(String auditEventKey) {
    this.customerIdWithSuffix = auditEventKey;
  }

  @DynamoDBRangeKey(attributeName = DBMetaData.AUDIT_EVENTS_RANGE_KEY_NAME)
  public Long getTimestampUTC() {
    return timestampUTC;
  }

  private static String buildKey(int customerId) {
    return buildKey(customerId, new Random().nextInt(DBMetaData.RANDOM_SUFFIX_MAX));
  }

  private static String buildKey(int customerId, int suffix) {
    return customerId + DBMetaData.AUDIT_EVENTS_HASH_KEY_SEPARATOR + suffix;
  }
}
