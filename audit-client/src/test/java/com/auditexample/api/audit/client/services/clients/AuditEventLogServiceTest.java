package com.auditexample.api.audit.client.services.clients;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.auditexample.api.audit.client.domain.vo.AuditEventDetailsVO;
import com.auditexample.api.audit.client.domain.vo.AuditEventVO;
import com.auditexample.api.audit.client.publishers.AuditEventsPublisher;
import com.auditexample.api.audit.client.security.token.TokenConstants;
import com.auditexample.api.audit.client.services.CustomerService;
import com.auditexample.api.audit.client.services.EmployeeService;
import com.auditexample.api.audit.client.services.TimezoneService;
import com.auditexample.api.audit.client.services.oauth2.Oauth2ClientTokenService;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditEventLogServiceTest {

  @Mock
  private AuditEventsPublisher auditEventsPublisher;
  @Mock
  private Oauth2ClientTokenService oauth2ClientTokenService;
  @Mock
  private CustomerService customerService;
  @Mock
  private EmployeeService employeeService;
  @Mock
  private TimezoneService timezoneService;

  @InjectMocks
  private AuditEventLogService auditEventLogService;

  @Captor
  ArgumentCaptor<AuditEventVO> auditEventCaptor;


  @Test
  void logEvent() {
    AuditEventVO auditEvent = new AuditEventVO();
    auditEvent.setGroupId(nextInt());
    AuditEventDetailsVO eventDetails = new AuditEventDetailsVO();
    eventDetails.setEventDetails(randomAlphanumeric(30));
    eventDetails.setEventType(randomAlphabetic(20));
    eventDetails.setObjectId(nextInt());
    eventDetails.setObjectDetails(randomAlphanumeric(100));
    eventDetails.setParentRefId(nextInt());
    eventDetails.setPropertyName(randomAlphabetic(20));
    eventDetails.setPropertyValue(randomAlphanumeric(50));
    auditEvent.setEvent(eventDetails);

    var employeeId = nextInt();
    var customerId = nextInt();
    var timestamp = OffsetDateTime.now();
    when(oauth2ClientTokenService.getTokenValue(anyString())).thenReturn(randomAlphanumeric(100));
    when(employeeService.getEmployeeId()).thenReturn(employeeId);
    when(customerService.getCustomerIdForEmployee(eq(employeeId))).thenReturn(Optional.of(customerId));
    when(timezoneService.getTimestamp(any(), eq(employeeId))).thenReturn(timestamp);

    auditEventLogService.logEvent(auditEvent);

    verify(oauth2ClientTokenService, times(1)).getTokenValue(TokenConstants.UAA_CLIENT_REGISTRATION_ID);
    verify(auditEventsPublisher, times(1)).publish(auditEventCaptor.capture(), anyString());
    verify(employeeService, times(1)).getEmployeeId();
    verify(customerService, times(1)).getCustomerIdForEmployee(auditEvent.getEmployeeId());
    verify(timezoneService, times(1)).getTimestamp(any(), eq(auditEvent.getEmployeeId()));

    var actualAuditEvent = auditEventCaptor.getValue();
    assertEquals(employeeId, actualAuditEvent.getEmployeeId());
    assertEquals(customerId, actualAuditEvent.getCustomerId());
    assertEquals(timestamp, actualAuditEvent.getEvent().getTimestamp());
  }

  @Test
  void logEventWithAllParamsProvided() {
    AuditEventVO auditEvent = new AuditEventVO();
    auditEvent.setCustomerId(nextInt());
    auditEvent.setEmployeeId(nextInt());
    auditEvent.setGroupId(nextInt());
    AuditEventDetailsVO eventDetails = new AuditEventDetailsVO();
    eventDetails.setTimestamp(OffsetDateTime.now());
    eventDetails.setEventDetails(randomAlphanumeric(30));
    eventDetails.setEventType(randomAlphabetic(20));
    eventDetails.setObjectId(nextInt());
    eventDetails.setObjectDetails(randomAlphanumeric(100));
    eventDetails.setParentRefId(nextInt());
    eventDetails.setPropertyName(randomAlphabetic(20));
    eventDetails.setPropertyValue(randomAlphanumeric(50));
    auditEvent.setEvent(eventDetails);

    when(oauth2ClientTokenService.getTokenValue(anyString())).thenReturn(randomAlphanumeric(100));

    auditEventLogService.logEvent(auditEvent);

    verify(oauth2ClientTokenService, times(1)).getTokenValue(TokenConstants.UAA_CLIENT_REGISTRATION_ID);
    verify(auditEventsPublisher, times(1)).publish(eq(auditEvent), anyString());
    verifyNoInteractions(employeeService);
    verifyNoInteractions(customerService);
    verifyNoInteractions(timezoneService);
  }
}