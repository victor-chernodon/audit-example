package com.auditexample.api.audit.client.services.clients;

import com.auditexample.api.audit.client.domain.vo.AuditEventDetailsVO;
import com.auditexample.api.audit.client.domain.vo.AuditEventVO;
import com.auditexample.api.audit.client.publishers.AuditEventsPublisher;
import com.auditexample.api.audit.client.security.token.TokenConstants;
import com.auditexample.api.audit.client.services.CustomerService;
import com.auditexample.api.audit.client.services.EmployeeService;
import com.auditexample.api.audit.client.services.TimezoneService;
import com.auditexample.api.audit.client.services.oauth2.Oauth2ClientTokenService;
import java.time.OffsetDateTime;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created at Auditexample<br> User: Victor Chernodon<br> Date: 2020-04-15<br>
 */
@Service
@Slf4j
public class AuditEventLogService  {

  private final AuditEventsPublisher auditEventsPublisher;
  private final Oauth2ClientTokenService oauth2ClientTokenService;
  private final CustomerService customerService;
  private final EmployeeService employeeService;
  private final TimezoneService timezoneService;

  @Autowired
  public AuditEventLogService(
      AuditEventsPublisher auditEventsPublisher,
      Oauth2ClientTokenService oauth2ClientTokenService,
      CustomerService customerService,
      EmployeeService employeeService,
      TimezoneService timezoneService) {
    this.auditEventsPublisher = auditEventsPublisher;
    this.oauth2ClientTokenService = oauth2ClientTokenService;
    this.customerService = customerService;
    this.employeeService = employeeService;
    this.timezoneService = timezoneService;
  }

  public void logEvent(AuditEventVO auditEventVO) {
    if (Objects.isNull(auditEventVO.getEmployeeId())) {
      auditEventVO.setEmployeeId(employeeService.getEmployeeId());
    }
    if (Objects.isNull(auditEventVO.getCustomerId())
        && Objects.nonNull(auditEventVO.getEmployeeId())) {
      auditEventVO.setCustomerId(
          customerService.getCustomerIdForEmployee(auditEventVO.getEmployeeId()).orElse(null));
    }
    if (Objects.isNull(auditEventVO.getEvent().getTimestamp())) {
      OffsetDateTime timestamp = timezoneService.getTimestamp(OffsetDateTime.now(), auditEventVO.getEmployeeId());
      var auditEventDetails =
          AuditEventDetailsVO.builder()
              .eventType(auditEventVO.getEvent().getEventType())
              .eventDetails(auditEventVO.getEvent().getEventDetails())
              .objectId(auditEventVO.getEvent().getObjectId())
              .objectDetails(auditEventVO.getEvent().getObjectDetails())
              .parentRefId(auditEventVO.getEvent().getParentRefId())
              .propertyName(auditEventVO.getEvent().getPropertyName())
              .propertyValue(auditEventVO.getEvent().getPropertyValue())
              .timestamp(timestamp)
              .build();
      auditEventVO.setEvent(auditEventDetails);
    }
    auditEventsPublisher.publish(
        auditEventVO,
        oauth2ClientTokenService.getTokenValue(TokenConstants.UAA_CLIENT_REGISTRATION_ID));
    log.debug("logged event: {}", auditEventVO);
  }

}
