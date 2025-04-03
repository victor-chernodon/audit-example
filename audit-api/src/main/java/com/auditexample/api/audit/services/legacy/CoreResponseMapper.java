package com.auditexample.api.audit.services.legacy;

import static java.util.stream.Collectors.toUnmodifiableList;

import com.google.gson.JsonObject;
import com.auditexample.api.audit.common.domain.pagination.PaginatedResult;
import com.auditexample.api.audit.common.domain.pagination.Pagination;
import com.auditexample.api.audit.common.domain.rpo.AuditEventsRPO;
import com.auditexample.api.audit.common.domain.vo.AuditEventDetailsVO;
import com.auditexample.api.audit.common.domain.vo.AuditEventVO;
import com.auditexample.api.audit.domain.core.ScheduleLogResponse;
import com.auditexample.api.audit.domain.core.ShiftPropertyName;
import com.auditexample.api.audit.domain.core.pagination.PaginatedCoreResult;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class CoreResponseMapper {

  private static final String SHIFT_UPDATED = "schedule.shiftUpdated";

  private CoreResponseMapper() {}

  static PaginatedResult<AuditEventsRPO> mapToAuditEvents(
      PaginatedCoreResult<List<ScheduleLogResponse>> coreResult, ZoneId timezone) {
    return new PaginatedResult<>(mapToAuditEvents(coreResult.getLogItems(), timezone),
        new Pagination(coreResult.getTotalCount(), coreResult.getPageNumber(),
            coreResult.getTotalPages()));
  }

  private static AuditEventsRPO mapToAuditEvents(List<ScheduleLogResponse> coreLogItems, ZoneId timezone) {
    if (Objects.isNull(coreLogItems)) {
      return null;
    }
    var auditEvents = new AuditEventsRPO();
    auditEvents.setAuditEvents(coreLogItems.stream().map(convertToAuditEvent(timezone)).collect(toUnmodifiableList()));
    return auditEvents;
  }

  private static Function<ScheduleLogResponse, AuditEventVO> convertToAuditEvent(ZoneId timezone) {
    return scheduleLogResponse -> {
      AuditEventDetailsVO auditEventDetailsVO = new AuditEventDetailsVO();
      auditEventDetailsVO.setEventType("schedule");
      switch (scheduleLogResponse.getProperty()) {
        case SHIFT_CREATED:
        case SHIFT_DELETED:
          auditEventDetailsVO.setEventDetails(scheduleLogResponse.getProperty().textName());
          auditEventDetailsVO.setObjectDetails(scheduleLogObjectDetails(scheduleLogResponse));
          break;
        case EMPLOYEE_ID:
        case GRABB_ID:
        case SHIFT_TYPE:
        case ACCOUNT_NO:
        case COST_CENTRE:
        case PROJECT_NO:
        case COMMENT:
        case BEGIN_DATE:
        case BEGIN_TIME:
        case END_DATE:
        case END_TIME:
        case BREAK_BEGIN:
        case BREAK_END:
        case BREAK_BEGIN2:
        case BREAK_END2:
        case BREAK_BEGIN3:
        case BREAK_END3:
        case BREAK_BEGIN4:
        case BREAK_END4:
          auditEventDetailsVO.setEventDetails(SHIFT_UPDATED);
          auditEventDetailsVO.setPropertyName(scheduleLogResponse.getProperty().textName());
          auditEventDetailsVO.setPropertyValue(scheduleLogResponse.getStringNewValue());
          auditEventDetailsVO.setObjectDetails(scheduleLogObjectDetails(scheduleLogResponse));
          break;
        case SECTION:
          auditEventDetailsVO.setEventDetails(SHIFT_UPDATED);
          auditEventDetailsVO.setPropertyName(scheduleLogResponse.getProperty().textName());
          auditEventDetailsVO.setPropertyValue((scheduleLogResponse.getNumericNewValue() != null
              && scheduleLogResponse.getNumericNewValue().longValue() > 1)
              ? scheduleLogResponse.getStringNewValue() : "");
          auditEventDetailsVO.setObjectDetails(scheduleLogObjectDetails(scheduleLogResponse));
          break;
        default:
          auditEventDetailsVO.setEventDetails(SHIFT_UPDATED);
          auditEventDetailsVO.setPropertyName(scheduleLogResponse.getProperty().textName());
          auditEventDetailsVO.setPropertyValue(
              Objects.isNull(scheduleLogResponse.getNumericNewValue()) ? scheduleLogResponse
                  .getStringNewValue() : scheduleLogResponse.getNumericNewValue().toString());
          auditEventDetailsVO.setObjectDetails(scheduleLogObjectDetails(scheduleLogResponse));
      }
      auditEventDetailsVO.setTimestamp(
          scheduleLogResponse.getTimestamp().atZone(timezone).toOffsetDateTime());
      auditEventDetailsVO.setObjectId((int) scheduleLogResponse.getShiftId());
      AuditEventVO auditEventVO = new AuditEventVO();
      auditEventVO.setEmployeeId(scheduleLogResponse.getEmployeeId());
      auditEventVO.setEvent(auditEventDetailsVO);
      return auditEventVO;
    };
  }

  private static String scheduleLogObjectDetails(ScheduleLogResponse scheduleLogEntry) {
    JsonObject objectDetails = new JsonObject();
    objectDetails.addProperty(ShiftPropertyName.BEGIN_DATE.textName(), scheduleLogEntry.getBeginDate().toString());
    objectDetails.addProperty(ShiftPropertyName.BEGIN_TIME.textName(), scheduleLogEntry.getBeginTime().toString());
    objectDetails.addProperty(ShiftPropertyName.END_TIME.textName(), scheduleLogEntry.getEndTime().toString());
    objectDetails.addProperty("shift.shiftTypeName", scheduleLogEntry.getShiftTypeName());
    return objectDetails.toString();
  }

}
