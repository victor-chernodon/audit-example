package com.auditexample.api.audit.client.services;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class CustomerService {

  private static final long DURATION_IN_MILLIS = 15000;

  private final WebClient webClient;

  @Autowired
  public CustomerService(@Qualifier("customerWebClient") WebClient webClient) {
    this.webClient = webClient;
  }

  public Optional<Integer> getCustomerIdForEmployee(int employeeId) {
    var customerIdByEmployeeId =
        webClient
            .post()
            .uri(uriBuilder -> uriBuilder.path("/customers/employee-ids").build(employeeId))
            .contentType(MediaType.APPLICATION_JSON)
            .body(fromObject(Set.of(employeeId)))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<Integer, Integer>>() {})
            .block(Duration.ofMillis(DURATION_IN_MILLIS));
    log.debug(
        "Customer id {} is fetched for employee {}",
        employeeId,
        customerIdByEmployeeId == null ? null : customerIdByEmployeeId.get(employeeId));
    return Optional.ofNullable(customerIdByEmployeeId)
        .map(customerIdMapping -> customerIdMapping.get(employeeId));
  }
}
