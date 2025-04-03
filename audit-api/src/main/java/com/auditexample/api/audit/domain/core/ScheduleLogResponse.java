package com.auditexample.api.audit.domain.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleLogResponse {

  @JsonIgnore
  private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

  private final Long id;
  private final Integer unitId;
  private final long shiftId;
  private final ShiftPropertyName property;
  private final String stringNewValue;
  private final Number numericNewValue;
  private final Integer employeeId;
  private final String employeeFirstName;
  private final String employeeLastName;
  private final LocalDateTime timestamp;
  private final String shiftTypeName;
  private final LocalDate beginDate;
  private final LocalTime beginTime;
  private final LocalTime endTime;
  private final boolean changedByManager;

  @JsonCreator
  public ScheduleLogResponse(
      @JsonProperty("id") Long id,
      @JsonProperty("restId") Integer unitId,
      @JsonProperty("scheduleId") long shiftId,
      @JsonProperty("fieldNo") ShiftPropertyName property,
      @JsonProperty("strNewValue") String stringNewValue,
      @JsonProperty("numNewValue") Number numericNewValue,
      @JsonProperty("changerId") Integer employeeId,
      @JsonProperty("givenName") String employeeFirstName,
      @JsonProperty("familyName") String employeeLastName,
      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN) @JsonProperty("ts")
          LocalDateTime timestamp,
      @JsonProperty("categoryName") String shiftTypeName,
      @JsonProperty("begDate") LocalDate beginDate,
      @JsonProperty("begTime") LocalTime beginTime,
      @JsonProperty("endTime") LocalTime endTime,
      @JsonProperty("changedByManager") boolean changedByManager) {
    this.id = id;
    this.unitId = unitId;
    this.shiftId = shiftId;
    this.property = property;
    this.stringNewValue = stringNewValue;
    this.numericNewValue = numericNewValue;
    this.employeeId = employeeId;
    this.employeeFirstName = employeeFirstName;
    this.employeeLastName = employeeLastName;
    this.timestamp = timestamp;
    this.shiftTypeName = shiftTypeName;
    this.beginDate = beginDate;
    this.beginTime = beginTime;
    this.endTime = endTime;
    this.changedByManager = changedByManager;
  }
}
