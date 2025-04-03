package com.auditexample.api.audit.services;

import com.auditexample.api.audit.configs.CaffeineCacheConfig.FiveMinutesCache;
import java.time.Duration;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@ConditionalOnExpression("!'${organisation.service.url:}'.isEmpty()")
@CacheConfig(cacheManager = "caffeineCacheManager")
public class OrganisationApiService {

  private static final long DURATION_IN_MILLIS = 50000;

  private final WebClient webClient;

  @Autowired
  public OrganisationApiService(@Value("${organisation.service.url}") String organisationServiceUrl) {
    this.webClient = WebClient.builder().baseUrl(organisationServiceUrl).build();
  }

  @Cacheable(cacheNames = FiveMinutesCache.UNIT_ID_BY_GROUP_ID, unless = "#result == null")
  public Optional<Integer> getUnitIdByGroupId(Integer groupId) {
    try {
      var unitId = webClient.get()
          .uri(uriBuilder ->
              uriBuilder
                  .path("/group-hierarchy/groups/{groupId}/unit-id-by-group-id")
                  .build(groupId))
          .retrieve()
          .bodyToMono(Integer.class)
          .block(Duration.ofMillis(DURATION_IN_MILLIS));
      log.debug("Fetched unit id {} for group id {}", unitId, groupId);
      return Optional.ofNullable(unitId);
    } catch (Exception ex) {
      log.error("Unable to fetch unit id for group id {}", groupId, ex);
    }
    return Optional.empty();
  }

  @Cacheable(cacheNames = FiveMinutesCache.TIMEZONE_BY_GROUP, unless = "#result == null")
  public String getTimeZoneForGroup(Integer groupId) {
    try {
      var timezone = webClient.get()
          .uri(uriBuilder ->
              uriBuilder
                  .path("/group-internal/groups/{groupId}/timezone")
                  .build(groupId))
          .retrieve()
          .bodyToMono(String.class)
          .block(Duration.ofMillis(DURATION_IN_MILLIS));
      log.debug("Fetched timezone {} for group id {}", timezone, groupId);
      return timezone;
    } catch (Exception ex) {
      log.error("Unable to fetch unit id for group id {}", groupId, ex);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Unable to fetch timezone for group ID " + groupId);
    }
  }
}