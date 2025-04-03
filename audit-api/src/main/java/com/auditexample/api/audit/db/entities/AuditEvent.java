package com.auditexample.api.audit.db.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.auditexample.api.audit.db.DBMetaData;
import java.time.OffsetDateTime;
import java.util.Objects;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@DynamoDBTable(tableName = DBMetaData.AUDIT_EVENTS_TABLE_NAME)
public class AuditEvent {

  private AuditEventId auditEventId;
  private Integer groupId;
  private Integer employeeId;
  private String eventType;
  private String eventDetails;
  private String objectDetails;
  private String propertyName;
  private String propertyValue;
  private AuditEventHashKeyWithPrefix objectIdHashKey;
  private AuditEventHashKeyWithPrefix parentRefIdHashKey;
  private OffsetDateTime timestamp;

  public AuditEvent(Integer customerId, OffsetDateTime timestamp, String eventType,
      Integer groupId, Integer employeeId, String eventDetails, String objectDetails,
      String propertyName, String propertyValue) {
    this.auditEventId = new AuditEventId(customerId, timestamp.toInstant().toEpochMilli());
    this.timestamp = timestamp;
    this.eventType = eventType;
    this.objectDetails = objectDetails;
    this.auditEventId = new AuditEventId(customerId, timestamp.toInstant().toEpochMilli());
    this.employeeId = employeeId;
    this.groupId = groupId;
    this.eventDetails = eventDetails;
    this.propertyName = propertyName;
    this.propertyValue = propertyValue;
  }

  public AuditEvent(Integer customerId, OffsetDateTime timestamp, String eventType, Integer groupId,
      Integer employeeId, Integer objectId, Integer parentRefIdHashKey, String eventDetails,
      String objectDetails, String propertyName, String propertyValue) {
    this(customerId, timestamp, eventType, groupId, employeeId, eventDetails,
        objectDetails, propertyName, propertyValue);
    this.objectIdHashKey =
        objectId == null ? null : new AuditEventHashKeyWithPrefix(customerId, objectId);
    this.parentRefIdHashKey =
        parentRefIdHashKey == null ? null : new AuditEventHashKeyWithPrefix(customerId,
            parentRefIdHashKey);
  }

  @DynamoDBHashKey(attributeName = DBMetaData.AUDIT_EVENTS_HASH_KEY_NAME)
  public String getAuditEventKey() {
    return auditEventId != null ? auditEventId.getAuditEventKey() : null;
  }

  public void setAuditEventKey(String auditEventKey) {
    if (auditEventId == null) {
      this.auditEventId = new AuditEventId();
    }
    auditEventId.setAuditEventKey(auditEventKey);
  }

  @DynamoDBRangeKey(attributeName = DBMetaData.AUDIT_EVENTS_RANGE_KEY_NAME)
  @DynamoDBIndexRangeKey(attributeName = DBMetaData.AUDIT_EVENTS_RANGE_KEY_NAME,
      globalSecondaryIndexNames =
          {DBMetaData.AUDIT_EVENTS_GSI_OBJECT_INDEX_NAME, DBMetaData.AUDIT_EVENTS_GSI_PARENT_REF_INDEX_NAME})
  public Long getTimestampUTC() {
    return auditEventId != null ? auditEventId.getTimestampUTC() : null;
  }

  public void setTimestampUTC(Long timestampUTC) {
    if (auditEventId == null) {
      auditEventId = new AuditEventId();
    }
    auditEventId.setTimestampUTC(timestampUTC);
  }

  @DynamoDBIgnore
  public Integer getCustomerId() {
    if (auditEventId == null) {
      return null;
    }
    return Integer.valueOf(
        auditEventId.getAuditEventKey().split(DBMetaData.AUDIT_EVENTS_HASH_KEY_SEPARATOR)[0]);
  }

  @DynamoDBIgnore
  public Integer getObjectId() {
    return objectIdHashKey == null ? null : objectIdHashKey.getKey();
  }

  @DynamoDBIgnore
  public Integer getParentRefId() {
    return parentRefIdHashKey == null ? null : parentRefIdHashKey.getKey();
  }

  @DynamoDBAttribute(attributeName = DBMetaData.AUDIT_EVENTS_EVENT_TYPE_NAME)
  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public void setAuditEventId(AuditEventId auditEventId) {
    this.auditEventId = auditEventId;
  }

  @DynamoDBIgnore
  public AuditEventId getAuditEventId() {
    return auditEventId;
  }

  @DynamoDBAttribute(attributeName = DBMetaData.AUDIT_EVENTS_GROUP_ID_NAME)
  public Integer getGroupId() {
    return groupId;
  }

  public void setGroupId(Integer groupId) {
    this.groupId = groupId;
  }

  @DynamoDBAttribute(attributeName = DBMetaData.AUDIT_EVENTS_EMPLOYEE_ID_NAME)
  public Integer getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(Integer employeeId) {
    this.employeeId = employeeId;
  }

  @DynamoDBAttribute(attributeName = DBMetaData.AUDIT_EVENTS_EVENT_DETAILS_NAME)
  public String getEventDetails() {
    return eventDetails;
  }

  public void setEventDetails(String eventDetails) {
    this.eventDetails = eventDetails;
  }

  @DynamoDBIndexHashKey(attributeName = DBMetaData.AUDIT_EVENTS_OBJECT_ID_HASH_KEY_NAME,
      globalSecondaryIndexName = DBMetaData.AUDIT_EVENTS_GSI_OBJECT_INDEX_NAME)
  public String getObjectIdHashKey() {
    return objectIdHashKey == null ? null : objectIdHashKey.getAuditEventHashKeyWithPrefix();
  }

  public void setObjectIdHashKey(String objectIdHash) {
    this.objectIdHashKey = new AuditEventHashKeyWithPrefix(objectIdHash);
  }

  public void setObjectIdHashKey(int customerId, int objectId) {
    this.objectIdHashKey = new AuditEventHashKeyWithPrefix(customerId, objectId);
  }

  @DynamoDBIndexHashKey(attributeName = DBMetaData.AUDIT_EVENTS_PARENT_OBJECT_REFERENCE_ID_HASH_KEY_NAME,
      globalSecondaryIndexName = DBMetaData.AUDIT_EVENTS_GSI_PARENT_REF_INDEX_NAME)
  public String getParentRefIdHashKey() {
    return parentRefIdHashKey == null ? null : parentRefIdHashKey.getAuditEventHashKeyWithPrefix();
  }

  public void setParentRefIdHashKey(String parentRefIdHashKey) {
    this.parentRefIdHashKey = new AuditEventHashKeyWithPrefix(parentRefIdHashKey);
  }

  public void setParentRefIdHashKey(int customerId, int parentRefId) {
    this.parentRefIdHashKey = new AuditEventHashKeyWithPrefix(customerId, parentRefId);
  }

  @DynamoDBAttribute(attributeName = DBMetaData.AUDIT_EVENTS_EVENT_TIMESTAMP_NAME)
  @DynamoDBTypeConverted(converter = OffsetDateTimeConverter.class)
  public OffsetDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
  }

  @DynamoDBAttribute(attributeName = DBMetaData.AUDIT_EVENTS_OBJECT_DETAILS_NAME)
  public String getObjectDetails() {
    return objectDetails;
  }

  public void setObjectDetails(String objectDetails) {
    this.objectDetails = objectDetails;
  }

  @DynamoDBAttribute(attributeName = DBMetaData.AUDIT_EVENTS_PROPERTY_NAME)
  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  @DynamoDBAttribute(attributeName = DBMetaData.AUDIT_EVENTS_PROPERTY_VALUE_NAME)
  public String getPropertyValue() {
    return propertyValue;
  }

  public void setPropertyValue(String propertyValue) {
    this.propertyValue = propertyValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AuditEvent that = (AuditEvent) o;
    return getAuditEventId().equals(that.getAuditEventId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getAuditEventId());
  }
}
