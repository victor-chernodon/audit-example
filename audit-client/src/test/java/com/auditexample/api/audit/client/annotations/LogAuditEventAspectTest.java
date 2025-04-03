package com.auditexample.api.audit.client.annotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auditexample.api.audit.client.domain.vo.AuditEventVO;
import com.auditexample.api.audit.client.services.TimezoneService;
import com.auditexample.api.audit.client.services.clients.AuditEventLogService;
import java.time.OffsetDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
class LogAuditEventAspectTest {

  @Mock
  private AuditEventLogService auditEventLogService;
  @Mock
  private TimezoneService timezoneService;
  @Mock
  private ProceedingJoinPoint joinPoint;
  @Mock
  private MethodSignature methodSignature;
  @Mock
  private ExecutorService auditLogSubmittionExecutor;

  @Captor
  private ArgumentCaptor<AuditEventVO> auditEventArgumentCaptor;

  @InjectMocks
  private LogAuditEventAspect aspect;

  @BeforeEach
  void setUp() {
    implementAsDirectExecutor(auditLogSubmittionExecutor);
  }

  @Test
  void defaultMinimalParams() throws Throwable {
    var timestamp = OffsetDateTime.now();
    when(timezoneService.getTimestamp(any(), eq(456))).thenReturn(timestamp);
    aspect.logAuditEvent(
        joinPoint, getClass().getMethod("loggedMethod1").getAnnotation(LogAuditEvent.class));
    verify(joinPoint).proceed();
    TimeUnit.SECONDS.sleep(1);
    verify(timezoneService).getTimestamp(any(), eq(456));
    verify(auditEventLogService).logEvent(auditEventArgumentCaptor.capture());
    assertEquals(123, auditEventArgumentCaptor.getValue().getCustomerId().intValue());
    assertEquals(456, auditEventArgumentCaptor.getValue().getEmployeeId().intValue());
    assertEquals("type", auditEventArgumentCaptor.getValue().getEvent().getEventType());
    assertEquals(timestamp, auditEventArgumentCaptor.getValue().getEvent().getTimestamp());
  }

  @Test
  void allParams() throws Throwable {
    var timestamp = OffsetDateTime.now();
    when(timezoneService.getTimestamp(any(), eq(456))).thenReturn(timestamp);
    aspect.logAuditEvent(
        joinPoint, getClass().getMethod("loggedMethod2").getAnnotation(LogAuditEvent.class));
    verify(joinPoint).proceed();
    TimeUnit.SECONDS.sleep(1);
    verify(timezoneService).getTimestamp(any(), eq(456));
    verify(auditEventLogService).logEvent(auditEventArgumentCaptor.capture());
    assertEquals(123, auditEventArgumentCaptor.getValue().getCustomerId().intValue());
    assertEquals(456, auditEventArgumentCaptor.getValue().getEmployeeId().intValue());
    assertEquals(789, auditEventArgumentCaptor.getValue().getGroupId().intValue());
    assertEquals("type", auditEventArgumentCaptor.getValue().getEvent().getEventType());
    assertEquals("details", auditEventArgumentCaptor.getValue().getEvent().getEventDetails());
    assertEquals(timestamp, auditEventArgumentCaptor.getValue().getEvent().getTimestamp());
  }

  @LogAuditEvent(customerId = 123, employeeId = 456, eventType = "type")
  public void loggedMethod1() {}

  @LogAuditEvent(customerId = 123, employeeId = 456, groupId = "789", eventType = "type", eventDetails = "details")
  public void loggedMethod2() {}

  private void implementAsDirectExecutor(ExecutorService executor) {
    doAnswer((Answer<Object>) invocation -> {
      Object[] args = invocation.getArguments();
      Runnable runnable = (Runnable)args[0];
      runnable.run();
      return null;
    }).when(executor).submit(any(Runnable.class));
  }

}