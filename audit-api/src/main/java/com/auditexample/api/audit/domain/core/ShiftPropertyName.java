package com.auditexample.api.audit.domain.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Objects;

public enum ShiftPropertyName {

  SHIFT_CREATED(0, "schedule.shiftCreated"),
  BEGIN_DATE(1, "shift.beginDate"),
  BEGIN_TIME(2, "shift.beginTime"),
  END_DATE(3, "shift.endDate"),
  END_TIME(4, "shift.endTime"),
  BREAK_BEGIN(5, "shift.breakBegin"),
  BREAK_END(6, "shift.breakEnd"),
  BREAK_BEGIN2(7, "shift.breakBegin2"),
  BREAK_END2(8, "shift.breakEnd2"),
  BREAK_BEGIN3(9, "shift.breakBegin3"),
  BREAK_END3(10, "shift.breakEnd3"),
  BREAK_BEGIN4(11, "shift.breakBegin4"),
  BREAK_END4(12, "shift.breakEnd4"),
  EMPLOYEE_ID(13, "shift.employeeId"),
  GRABBABLE(14, "shift.grabbable"),
  GRABB_ID(15, "shift.grabbId"),
  SHIFT_TYPE(16, "shift.shiftType"),
  COMMENT(17, "shift.comment"),
  SECTION(18, "shift.section"),
  FIXED_TASK(19, "shift.fixedTask"),
  TASK_HOURS(20, "shift.taskHours"),
  IN_CALC(21, "shift.inCalculation"),
  ACCOUNT_NO(23, "shift.accountNumber"),
  COST_CENTRE(24, "shift.costCentre"),
  PROJECT_NO(25, "shift.projectNumber"),
  SHIFT_DELETED(26, "schedule.shiftRemoved");

  private final int value;
  private final String textName;

  ShiftPropertyName(int value, String textName) {
    this.value = value;
    this.textName = textName;
  }

  public int value() {
    return value;
  }

  public String textName() {
    return textName;
  }

  @JsonCreator
  public static ShiftPropertyName fromValue(Integer value) {
    if (Objects.nonNull(value)) {
      for (ShiftPropertyName shiftPropertyName : ShiftPropertyName.values()) {
        if (value == shiftPropertyName.value) {
          return shiftPropertyName;
        }
      }
    }
    return null;
  }

}
