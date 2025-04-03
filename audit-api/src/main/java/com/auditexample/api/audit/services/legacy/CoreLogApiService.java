package com.auditexample.api.audit.services.legacy;

import com.auditexample.api.audit.common.domain.pagination.PaginatedResult;
import com.auditexample.api.audit.common.domain.rpo.AuditEventsRPO;
import com.auditexample.api.audit.common.domain.rqo.TimeMillisRange;
import com.auditexample.api.audit.domain.core.ScheduleLogResponse;
import com.auditexample.api.audit.domain.core.pagination.PaginatedCoreResult;
import com.auditexample.api.audit.services.OrganisationApiService;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class CoreLogApiService {

  private static final long DURATION_IN_MILLIS = 200000;

  private WebClient coreClient;
  private OrganisationApiService organisationApiService;

  public PaginatedResult<AuditEventsRPO> listEvents(int customerId, int groupUnitId, CoreLogEventType eventType,
      TimeMillisRange timeRange, int currentPage, int pageSize) {
    if (Objects.isNull(coreClient) || Objects.isNull(organisationApiService)) {
      log.warn("Either Core or organisation client is not configured");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Core audit client is not configured");
    }
    var unitId = organisationApiService.getUnitIdByGroupId(groupUnitId)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Unit cannot be found for given group"));
    ZoneId zoneId = getZoneIdForGroup(groupUnitId);
    var startDate = Instant.ofEpochMilli(timeRange.getBegin()).atZone(zoneId).toLocalDate();
    var endDate = Instant.ofEpochMilli(timeRange.getEnd()).atZone(zoneId).toLocalDate();
    if (eventType == CoreLogEventType.SCHEDULE) {
      var coreEvents = fetchCoreScheduleLogs(customerId,
          unitId, startDate, endDate, currentPage, pageSize);
      return coreEvents != null ? CoreResponseMapper.mapToAuditEvents(coreEvents, getZoneIdForGroup(groupUnitId)) : null;
    } else {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported event type");
    }
  }

  private PaginatedCoreResult<List<ScheduleLogResponse>> fetchCoreScheduleLogs(int customerId, int unitId,
      LocalDate startDate, LocalDate endDate, int currentPage, int pageSize) {
    try {
      var coreAuditEvents = coreClient.get()
          .uri(uriBuilder ->
              uriBuilder
                  .path("/scheduleLogs/customers/{customerId}/units/{unitId}/fromDate/{startDate}/toDate/{endDate}/mode/1")
                  .queryParam("page", currentPage)
                  .queryParam("itemsPerPage", pageSize)
                  .build(customerId, unitId, startDate, endDate))
          .retrieve()
          .bodyToMono(new ParameterizedTypeReference<PaginatedCoreResult<List<ScheduleLogResponse>>>() {
          })
          .block(Duration.ofMillis(DURATION_IN_MILLIS));
      log.debug("{} Core log items loaded", coreAuditEvents == null ? 0 : coreAuditEvents.getLogItems().size());
      return coreAuditEvents;
    } catch (Exception ex) {
      log.error("Unable to fetch schedule logs from core", ex);
    }
    return null;
  }

  @Autowired(required = false)
  public void setClient(@Qualifier("coreAuditLogApiClient") WebClient webClient) {
    this.coreClient = webClient;
  }

  @Autowired(required = false)
  public void setOrganisationApiService(OrganisationApiService organisationApiService) {
    this.organisationApiService = organisationApiService;
  }

  private ZoneId getZoneIdForGroup(Integer groupId) {
    if (Objects.isNull(organisationApiService)) {
      log.error("Organisation service API is not configured");
      return ZoneId.systemDefault();
    }
    return ZoneId.of(organisationApiService.getTimeZoneForGroup(groupId));
  }
}
