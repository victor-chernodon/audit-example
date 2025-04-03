package com.auditexample.api.audit.client.domain;

public interface DomainConstants {

  String CUSTOMER_ID = "customer_id";
  String EVENT_TIMESTAMP_UTC = "timestamp_utc";
  String GROUP_ID = "group_id";
  String EMPLOYEE_ID = "employee_id";
  String HASH_KEY = "customer#eventType";

  String EVENTS = "events";
  String EVENT = "event";
  String EVENT_TYPE = "event_type";
  String EVENT_TIMESTAMP = "timestamp";
  String EVENT_DETAILS = "event_details";
  String EVENT_OBJECT_ID = "object_id";
  String EVENT_PARENT_REFERENCE_ID = "parent_ref_id";
  String EVENT_OBJECT_DETAILS = "object_details";

  String EVENTS_TIME_RANGE = "time_range";

  // DateTimeFormatter.ISO_DATE_TIME
  String TIMESTAMP_ISO_8601_ZONE_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSxxx";

  String EVENT_PROPERTY_NAME = "property_name";
  String EVENT_PROPERTY_VALUE = "property_value";
}
