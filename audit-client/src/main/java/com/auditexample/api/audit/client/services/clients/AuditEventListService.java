package com.auditexample.api.audit.client.services.clients;

import com.auditexample.api.audit.client.domain.pagination.PaginatedResult;
import com.auditexample.api.audit.client.domain.pagination.Pagination;
import com.auditexample.api.audit.client.domain.rpo.AuditEventsRPO;
import com.auditexample.api.audit.client.domain.rqo.CustomerAuditEventsRQO;
import com.auditexample.api.audit.client.domain.rqo.EmployeeAuditEventsRQO;
import com.auditexample.api.audit.client.domain.rqo.GroupAuditEventsRQO;
import com.auditexample.api.audit.client.domain.rqo.ObjectAuditEventsRQO;
import com.auditexample.api.audit.client.domain.rqo.ParentRefAuditEventsRQO;
import java.time.Duration;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Created at Auditexample<br> User: Victor Chernodon<br> Date: 2020-04-15<br>
 */
@Service
@Slf4j
public class AuditEventListService {

  private static final long DURATION_IN_MILLIS = 50000;

  @Autowired
  @Qualifier("auditLogWebClient")
  private WebClient webClient;

  public PaginatedResult<AuditEventsRPO> listEvents(
      CustomerAuditEventsRQO rqo, int page, int pageSize) {
    var auditEvents = webClient.get()
        .uri(uriBuilder ->
            uriBuilder
                .path("/customers/{customerId}/events")
                .queryParam("from", rqo.getTimeRange().getBegin())
                .queryParam("to", rqo.getTimeRange().getEnd())
                .queryParam("page", page)
                .queryParam("size", pageSize)
                .build(rqo.getCustomerId()))
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<PaginatedResult<AuditEventsRPO>>() {
        })
        .block(Duration.ofMillis(DURATION_IN_MILLIS));
    log.debug("{} events loaded", getNumberOfReceivedRecords(auditEvents));
    return auditEvents;
  }

  public PaginatedResult<AuditEventsRPO> listEvents(
      GroupAuditEventsRQO rqo, int page, int pageSize) {
    var auditEvents = webClient.get()
        .uri(uriBuilder ->
            uriBuilder
                .path("/customers/{customerId}/groups/{groupId}/events")
                .queryParam("from", rqo.getTimeRange().getBegin())
                .queryParam("to", rqo.getTimeRange().getEnd())
                .queryParam("page", page)
                .queryParam("size", pageSize)
                .build(rqo.getCustomerId(), rqo.getGroupId()))
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<PaginatedResult<AuditEventsRPO>>() {
        })
        .block(Duration.ofMillis(DURATION_IN_MILLIS));
    log.debug("{} events loaded", getNumberOfReceivedRecords(auditEvents));
    return auditEvents;
  }

  public PaginatedResult<AuditEventsRPO> listEvents(
      GroupAuditEventsRQO rqo, String eventType, int page, int pageSize) {
    var auditEvents = webClient.get()
        .uri(uriBuilder ->
            uriBuilder
                .path("/customers/{customerId}/groups/{groupId}/events")
                .queryParam("from", rqo.getTimeRange().getBegin())
                .queryParam("to", rqo.getTimeRange().getEnd())
                .queryParam("type", eventType)
                .queryParam("page", page)
                .queryParam("size", pageSize)
                .build(rqo.getCustomerId(), rqo.getGroupId()))
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<PaginatedResult<AuditEventsRPO>>() {
        })
        .block(Duration.ofMillis(DURATION_IN_MILLIS));
    log.debug("{} events loaded", getNumberOfReceivedRecords(auditEvents));
    return auditEvents;
  }

  public PaginatedResult<AuditEventsRPO> listEvents(
      EmployeeAuditEventsRQO rqo, int page, int pageSize) {
    var auditEvents = webClient.get()
        .uri(uriBuilder ->
            uriBuilder
                .path("/customers/{customerId}/groups/{groupId}/employees/{employeeId}/events")
                .queryParam("from", rqo.getTimeRange().getBegin())
                .queryParam("to", rqo.getTimeRange().getEnd())
                .queryParam("page", page)
                .queryParam("size", pageSize)
                .build(rqo.getCustomerId(), rqo.getGroupId(), rqo.getEmployeeId()))
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<PaginatedResult<AuditEventsRPO>>() {
        })
        .block(Duration.ofMillis(DURATION_IN_MILLIS));
    log.debug("{} events loaded", getNumberOfReceivedRecords(auditEvents));
    return auditEvents;
  }

  public PaginatedResult<AuditEventsRPO> listEvents(
      ParentRefAuditEventsRQO rqo, int page, int pageSize) {
    var auditEvents = webClient.get()
        .uri(uriBuilder ->
            uriBuilder
                .path("/customers/{customerId}/parent-references/{parentRef}/events")
                .queryParam("from", rqo.getTimeRange().getBegin())
                .queryParam("to", rqo.getTimeRange().getEnd())
                .queryParam("page", page)
                .queryParam("size", pageSize)
                .build(rqo.getCustomerId(), rqo.getParentRefId()))
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<PaginatedResult<AuditEventsRPO>>() {
        })
        .block(Duration.ofMillis(DURATION_IN_MILLIS));
    log.debug("{} events loaded", getNumberOfReceivedRecords(auditEvents));
    return auditEvents;
  }

  public PaginatedResult<AuditEventsRPO> listEvents(ObjectAuditEventsRQO rqo, int page, int pageSize) {
    var auditEvents = webClient.get()
        .uri(uriBuilder ->
            uriBuilder
                .path("/customers/{customerId}/objects/{objectId}/events")
                .queryParam("from", rqo.getTimeRange().getBegin())
                .queryParam("to", rqo.getTimeRange().getEnd())
                .queryParam("page", page)
                .queryParam("size", pageSize)
                .build(rqo.getCustomerId(), rqo.getObjectId()))
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<PaginatedResult<AuditEventsRPO>>() {
        })
        .block(Duration.ofMillis(DURATION_IN_MILLIS));
    log.debug("{} events loaded", getNumberOfReceivedRecords(auditEvents));
    return auditEvents;
  }

  private long getNumberOfReceivedRecords(@Nullable PaginatedResult<?> paginatedResult) {
    return Optional.ofNullable(paginatedResult)
        .map(PaginatedResult::getPagination)
        .map(Pagination::getTotalCount)
        .orElse(0L);
  }

}
