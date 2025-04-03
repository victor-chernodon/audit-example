package com.auditexample.api.audit.services.legacy;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CoreLogEventType {

  SCHEDULE("schedule");

  private final String value;

  CoreLogEventType(String value) {
    this.value = value;
  }

  @JsonValue
  public String value() {
    return value;
  }

  public static CoreLogEventType fromValue(String value) {
    for (CoreLogEventType coreLogEventType : CoreLogEventType.values()) {
      if (coreLogEventType.value.equalsIgnoreCase(value)) {
        return coreLogEventType;
      }
    }
    return null;
  }
}
