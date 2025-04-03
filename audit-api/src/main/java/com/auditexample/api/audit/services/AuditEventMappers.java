package com.auditexample.api.audit.services;

import com.auditexample.api.audit.db.entities.AuditEvent;
import com.auditexample.api.audit.common.domain.pagination.PaginatedResult;
import com.auditexample.api.audit.common.domain.pagination.Pagination;
import com.auditexample.api.audit.common.domain.rpo.AuditEventsRPO;
import com.auditexample.api.audit.common.domain.vo.AuditEventDetailsVO;
import com.auditexample.api.audit.common.domain.vo.AuditEventVO;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AuditEventMappers {

  public AuditEvent mapToAuditEvent(final AuditEventVO auditEventVO) {
    var event = auditEventVO.getEvent();
    return new AuditEvent(
        auditEventVO.getCustomerId(),
        event.getTimestamp(),
        event.getEventType(),
        auditEventVO.getGroupId(),
        auditEventVO.getEmployeeId(),
        event.getObjectId(),
        event.getParentRefId(),
        event.getEventDetails(),
        event.getObjectDetails(),
        event.getPropertyName(),
        event.getPropertyValue());
  }

  public AuditEventsRPO mapToCustomerAuditEventsRPO(List<AuditEvent> auditEvents) {
    return mapToAuditEventsRPO(auditEvents, true, false, false);
  }

  public AuditEventsRPO mapToGroupAuditEventsRPO(List<AuditEvent> auditEvents) {
    return mapToAuditEventsRPO(auditEvents, true, true, false);
  }

  public AuditEventsRPO mapToEmployeeAuditEventsRPO(List<AuditEvent> auditEvents) {
    return mapToAuditEventsRPO(auditEvents, true, true, true);
  }

  public PaginatedResult<AuditEventsRPO> mapToPaginatedCustomerAuditEventsRPO(
      PaginatedResult<List<AuditEvent>> auditEvents) {
    var auditEventsRPO = mapToAuditEventsRPO(auditEvents.getResult(), true, false, false);
    return new PaginatedResult<>(auditEventsRPO,
        new Pagination(auditEvents.getPagination().getTotalCount(),
            auditEvents.getPagination().getCurrentPage(),
            auditEvents.getPagination().getTotalPages()));
  }

  public PaginatedResult<AuditEventsRPO> mapToPaginatedGroupAuditEventsRPO(
      PaginatedResult<List<AuditEvent>> auditEvents) {
    var auditEventsRPO = mapToAuditEventsRPO(auditEvents.getResult(), true, true, false);
    return new PaginatedResult<>(auditEventsRPO,
        new Pagination(auditEvents.getPagination().getTotalCount(),
            auditEvents.getPagination().getCurrentPage(),
            auditEvents.getPagination().getTotalPages()));
  }

  public PaginatedResult<AuditEventsRPO> mapToPaginatedEmployeeAuditEventsRPO(
      PaginatedResult<List<AuditEvent>> auditEvents) {
    var auditEventsRPO = mapToAuditEventsRPO(auditEvents.getResult(), true, true, true);
    return new PaginatedResult<>(auditEventsRPO,
        new Pagination(auditEvents.getPagination().getTotalCount(),
            auditEvents.getPagination().getCurrentPage(),
            auditEvents.getPagination().getTotalPages()));
  }

  private AuditEventsRPO mapToAuditEventsRPO(
      List<AuditEvent> auditEvents,
      boolean byCustomer,
      boolean byGroup,
      boolean byEmployee) {
    return AuditEventsRPO.builder()
        .auditEvents(
            auditEvents.stream().map(
                auditEvent -> mapToAuditEventVO(auditEvent, byCustomer, byGroup, byEmployee))
                .collect(Collectors.toList()))
        .build();
  }

  private AuditEventVO mapToAuditEventVO(
      AuditEvent auditEvent,
      boolean skipCustomerId,
      boolean skipGroupId,
      boolean skipEmployeeId) {
    return AuditEventVO.builder()
        .customerId(skipCustomerId ? null : auditEvent.getCustomerId())
        .groupId(skipGroupId ? null : auditEvent.getGroupId())
        .employeeId(skipEmployeeId ? null : auditEvent.getEmployeeId())
        .event(mapToAuditEventDetailsVO(auditEvent))
        .build();
  }

  private AuditEventDetailsVO mapToAuditEventDetailsVO(AuditEvent auditEvent) {
    return AuditEventDetailsVO.builder()
        .eventType(auditEvent.getEventType())
        .eventDetails(auditEvent.getEventDetails())
        .objectId(auditEvent.getObjectId())
        .parentRefId(auditEvent.getParentRefId())
        .objectDetails(auditEvent.getObjectDetails())
        .propertyName(auditEvent.getPropertyName())
        .propertyValue(auditEvent.getPropertyValue())
        .timestamp(auditEvent.getTimestamp())
        .build();
  }

}
