package com.auditexample.api.audit.db;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2020-04-16<br/>
 */

public interface DBMetaData {

  String AUDIT_EVENTS_TABLE_NAME = "audit_events";
  String AUDIT_EVENTS_HASH_KEY_SEPARATOR = "#";
  String AUDIT_EVENTS_CUSTOMER = "customer";
  String AUDIT_EVENTS_HASH_KEY_NAME = AUDIT_EVENTS_CUSTOMER + AUDIT_EVENTS_HASH_KEY_SEPARATOR + "suffix";
  String AUDIT_EVENTS_GSI_OBJECT_INDEX_NAME = "ObjectIdIndex";
  String AUDIT_EVENTS_GSI_PARENT_REF_INDEX_NAME = "ParentRefIdIndex";
  String AUDIT_EVENTS_RANGE_KEY_NAME = "timestampUTC";
  String AUDIT_EVENTS_GROUP_ID_NAME = "groupId";
  String AUDIT_EVENTS_EMPLOYEE_ID_NAME = "employeeId";
  String AUDIT_EVENTS_EVENT_TYPE_NAME = "eventType";
  String AUDIT_EVENTS_EVENT_TIMESTAMP_NAME = "eventTimestamp";
  String AUDIT_EVENTS_EVENT_DETAILS_NAME = "eventDetails";
  String AUDIT_EVENTS_OBJECT_DETAILS_NAME = "objectDetails";
  String AUDIT_EVENTS_PROPERTY_NAME = "propertyName";
  String AUDIT_EVENTS_PROPERTY_VALUE_NAME = "propertyValue";
  String AUDIT_EVENTS_OBJECT_ID_HASH_KEY_NAME = AUDIT_EVENTS_CUSTOMER + AUDIT_EVENTS_HASH_KEY_SEPARATOR + "objectId";
  String AUDIT_EVENTS_PARENT_OBJECT_REFERENCE_ID_HASH_KEY_NAME = AUDIT_EVENTS_CUSTOMER + AUDIT_EVENTS_HASH_KEY_SEPARATOR + "parentRefId";
  String UNDERSCORE = "_";
  String SHARD_PREFIX = "sh";

  int RANDOM_SUFFIX_MAX = 10;
}
