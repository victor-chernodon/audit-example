package com.auditexample.api.audit.controllers;

import com.auditexample.api.audit.common.domain.pagination.PaginatedResult;
import com.auditexample.api.audit.common.domain.rpo.AuditEventsRPO;
import com.auditexample.api.audit.common.domain.rqo.CustomerAuditEventsRQO;
import com.auditexample.api.audit.common.domain.rqo.EmployeeAuditEventsRQO;
import com.auditexample.api.audit.common.domain.rqo.GroupAuditEventsRQO;
import com.auditexample.api.audit.common.domain.rqo.ObjectAuditEventsRQO;
import com.auditexample.api.audit.common.domain.rqo.ParentRefAuditEventsRQO;
import com.auditexample.api.audit.common.domain.rqo.TimeMillisRange;
import com.auditexample.api.audit.services.AuditEventGateway;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.time.Instant;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Api(tags = "Rest API for audit events management")
public class AuditEventController {

  private static final String DEFAULT_PAGE_SIZE = "20";

  private final AuditEventGateway auditEventGateway;

  @Autowired
  public AuditEventController(AuditEventGateway auditEventGateway) {
    this.auditEventGateway = auditEventGateway;
  }

  @GetMapping(value = "/customers/{customerId}/events",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Get audit events for given customer within certain time period",
      response = AuditEventsRPO.class, responseContainer = "List")
  @ResponseBody
  @PreAuthorize("hasRole(T(Roles.Modules).AUDIT_LOGS) and #apiClient.hasResourceScope(T(Resources).AUDIT_LOGS,T(Scopes).READ)")
  public PaginatedResult<AuditEventsRPO> getCustomerAuditEvents(
      @PathVariable Integer customerId,
      @RequestParam("from") Long from,
      @RequestParam(value = "to", required = false) Long to,
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "size", defaultValue = DEFAULT_PAGE_SIZE) int size) {

    CustomerAuditEventsRQO rqo = new CustomerAuditEventsRQO(
        customerId,
        buildTimeRange(from, to));
    log.debug("Getting events for: {}", rqo);

    return auditEventGateway.listEvents(rqo, page, size);

  }

  @GetMapping(value = "/customers/{customerId}/groups/{groupId}/events",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Get audit events for given customer group within certain time period",
      response = AuditEventsRPO.class, responseContainer = "List")
  @ResponseBody
  @PreAuthorize("hasRole(T(Roles.Modules).AUDIT_LOGS) and #apiClient.hasResourceScope(T(Resources).AUDIT_LOGS,T(Scopes).READ)")
  public PaginatedResult<AuditEventsRPO> getGroupAuditEvents(
      @PathVariable Integer customerId,
      @PathVariable Integer groupId,
      @RequestParam(value = "type", required = false) String eventType,
      @RequestParam("from") Long from,
      @RequestParam(value = "to", required = false) Long to,
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "size", defaultValue = DEFAULT_PAGE_SIZE) int size) {

    GroupAuditEventsRQO rqo = new GroupAuditEventsRQO(
        customerId,
        groupId,
        buildTimeRange(from, to));
    log.debug("Getting events for: {} and event type {}", rqo, eventType);

    if (StringUtils.isEmpty(eventType)) {
      return auditEventGateway.listEvents(rqo, page, size);
    }

    return auditEventGateway.listEvents(rqo, eventType, page, size);

  }

  @GetMapping(value = "/customers/{customerId}/groups/{groupId}/employees/{employeeId}/events",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Get audit events for given customer group employee within certain time period",
      response = AuditEventsRPO.class, responseContainer = "List")
  @ResponseBody
  @PreAuthorize("hasRole(T(Roles.Modules).AUDIT_LOGS) and #apiClient.hasResourceScope(T(Resources).AUDIT_LOGS,T(Scopes).READ)")
  public PaginatedResult<AuditEventsRPO> getGroupAuditEvents(
      @PathVariable Integer customerId,
      @PathVariable Integer groupId,
      @PathVariable Integer employeeId,
      @RequestParam("from") Long from,
      @RequestParam(value = "to", required = false) Long to,
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "size", defaultValue = DEFAULT_PAGE_SIZE) int size) {

    EmployeeAuditEventsRQO rqo = new EmployeeAuditEventsRQO(
        customerId,
        groupId,
        employeeId,
        buildTimeRange(from, to));
    log.debug("Getting events for: {}", rqo);

    return auditEventGateway.listEvents(rqo, page, size);
  }

  @GetMapping(value = "/customers/{customerId}/objects/{objectId}/events",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Get audit events for given object id within certain time period",
      response = AuditEventsRPO.class, responseContainer = "List")
  @ResponseBody
  @PreAuthorize("hasRole(T(Roles.Modules).AUDIT_LOGS) and #apiClient.hasResourceScope(T(Resources).AUDIT_LOGS,T(Scopes).READ)")
  public PaginatedResult<AuditEventsRPO> getObjectAuditEvents(
      @PathVariable Integer customerId,
      @PathVariable Integer objectId,
      @RequestParam("from") Long from,
      @RequestParam(value = "to", required = false) Long to,
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "size", defaultValue = DEFAULT_PAGE_SIZE) int size) {

    ObjectAuditEventsRQO rqo = new ObjectAuditEventsRQO(
        customerId,
        objectId,
        buildTimeRange(from, to));
    log.debug("Getting events for: {}", rqo);

    return auditEventGateway.listEvents(rqo, page, size);
  }

  @GetMapping(value = "/customers/{customerId}/parent-references/{parentRef}/events",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Get audit events for given parent object id within certain time period",
      response = AuditEventsRPO.class, responseContainer = "List")
  @ResponseBody
  @PreAuthorize("hasRole(T(Roles.Modules).AUDIT_LOGS) and #apiClient.hasResourceScope(T(Resources).AUDIT_LOGS,T(Scopes).READ)")
  public PaginatedResult<AuditEventsRPO> getParentRefsAuditEvents(
      @PathVariable Integer customerId,
      @PathVariable Integer parentRef,
      @RequestParam("from") Long from,
      @RequestParam(value = "to", required = false) Long to,
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "size", defaultValue = DEFAULT_PAGE_SIZE) int size) {

    ParentRefAuditEventsRQO rqo = new ParentRefAuditEventsRQO(
        customerId,
        parentRef,
        buildTimeRange(from, to));
    log.debug("Getting events for: {}", rqo);

    return auditEventGateway.listEvents(rqo, page, size);
  }

  private TimeMillisRange buildTimeRange(Long from, Long to) {
    log.debug("Time Range input: from - {}, to - {}", from, to);
    // End timestamp is optional, and if not provided would mean 'up until now' end time range
    return TimeMillisRange.of(
        from,
        Objects.isNull(to) ? Instant.now().toEpochMilli() : to);
  }

}



