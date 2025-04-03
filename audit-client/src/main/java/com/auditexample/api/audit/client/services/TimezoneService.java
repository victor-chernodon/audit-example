package com.auditexample.api.audit.client.services;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class TimezoneService {

  private static final long DURATION_IN_MILLIS = 10000;

  private final WebClient webClient;

  @Autowired
  public TimezoneService(@Qualifier("timeZoneWebClient") WebClient webClient) {
    this.webClient = webClient;
  }

  private String getTimezoneForEmployee(int employeeId) {
    var timezone = webClient.get()
        .uri(uriBuilder ->
            uriBuilder
                .path("/{employeeId}/timezone")
                .build(employeeId))
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<String>() {})
        .block(Duration.ofMillis(DURATION_IN_MILLIS));
    log.debug("timezone {} is fetched for employee {}", timezone, employeeId);
    return timezone;
  }

  public OffsetDateTime getTimestamp(OffsetDateTime timestamp, Integer employeeId) {
    var timezone = getTimezoneForEmployee(employeeId);
    return timestamp.atZoneSameInstant(ZoneId.of(timezone)).toOffsetDateTime();
  }

}
