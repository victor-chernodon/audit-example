package com.auditexample.api.audit.client.annotations;

import com.auditexample.api.audit.client.domain.vo.AuditEventDetailsVO;
import com.auditexample.api.audit.client.domain.vo.AuditEventVO;
import com.auditexample.api.audit.client.services.CustomerService;
import com.auditexample.api.audit.client.services.EmployeeService;
import com.auditexample.api.audit.client.services.TimezoneService;
import com.auditexample.api.audit.client.services.clients.AuditEventLogService;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Aspect
@Slf4j
public class LogAuditEventAspect {

  private final ExecutorService auditLogSubmittionExecutor;
  private final AuditEventLogService auditEventService;
  private final TimezoneService timezoneService;
  private final EmployeeService employeeService;
  private final CustomerService customerService;

  @Autowired
  public LogAuditEventAspect(
      @Qualifier("auditLogSubmissionPool") ExecutorService auditLogSubmittionExecutor,
      AuditEventLogService auditEventService,
      TimezoneService timezoneService,
      EmployeeService employeeService,
      CustomerService customerService) {
    this.auditLogSubmittionExecutor = auditLogSubmittionExecutor;
    this.auditEventService = auditEventService;
    this.timezoneService = timezoneService;
    this.employeeService = employeeService;
    this.customerService = customerService;
  }

  @Around("@annotation(logAuditEvent)")
  public Object logAuditEvent(ProceedingJoinPoint joinPoint, LogAuditEvent logAuditEvent) throws Throwable {
    Object returnValue = joinPoint.proceed();
    submitAuditLogEvent(joinPoint, logAuditEvent);
    return returnValue;
  }

  private void submitAuditLogEvent(ProceedingJoinPoint joinPoint, LogAuditEvent logAuditEvent) {
    Integer groupId = getGroupId(joinPoint, logAuditEvent);
    Integer employeeId = getEmployeeId(logAuditEvent);
    OffsetDateTime timestamp = timezoneService.getTimestamp(OffsetDateTime.now(), employeeId);
    auditLogSubmittionExecutor.submit(() -> {
      try {
        var customerId = getCustomerId(logAuditEvent, employeeId);
        if (Objects.nonNull(customerId) && customerId !=0) {
          var auditEvent = AuditEventVO.builder()
              .customerId(customerId)
              .groupId(groupId)
              .employeeId(employeeId)
              .event(AuditEventDetailsVO.builder()
                  .eventType(logAuditEvent.eventType())
                  .eventDetails(logAuditEvent.eventDetails())
                  .objectId(getObjectId(joinPoint, logAuditEvent))
                  .objectDetails(getObjectDetails(joinPoint, logAuditEvent))
                  .parentRefId(getParentRefId(joinPoint, logAuditEvent))
                  .propertyName(getPropertyName(logAuditEvent))
                  .propertyValue(getPropertyValue(joinPoint, logAuditEvent))
                  .timestamp(timestamp)
                  .build())
              .build();
          auditEventService.logEvent(auditEvent);
        } else {
          log.error("Unable to log event. No customer id provided");
        }

      } catch(Exception ex) {
        log.warn("Could not publish audit event", ex);
      }
    });
  }

  private Integer getEmployeeId(LogAuditEvent logAuditEvent) {
    Integer employeeId = null;
    if (logAuditEvent.employeeId() != 0) {
      employeeId = logAuditEvent.employeeId();
    } else {
      employeeId = employeeService.getEmployeeId();
    }
    return employeeId;
  }

  private Integer getCustomerId(LogAuditEvent logAuditEvent, int employeeId) {
    if (logAuditEvent.customerId() == 0) {
      //TODO: fetch from principle
      return customerService.getCustomerIdForEmployee(employeeId).orElse(null);
    } else {
      return logAuditEvent.customerId();
    }
  }

  private Integer getGroupId(ProceedingJoinPoint joinPoint, LogAuditEvent logAuditEvent) {
    if (!StringUtils.isEmpty(logAuditEvent.groupId())) {
      try {
        return parseExpression(logAuditEvent.groupId(), Integer.class, joinPoint);
      } catch(Exception ex) {
        log.error("Could not evaluate group id expression {}", logAuditEvent.groupId());
      }
    }
    return null;
  }

  private String getPropertyName(LogAuditEvent logAuditEvent) {
    if (!StringUtils.isEmpty(logAuditEvent.propertyName())) {
      return logAuditEvent.propertyName();
    }
    return null;
  }

  private String getPropertyValue(ProceedingJoinPoint joinPoint, LogAuditEvent logAuditEvent) {
    if (!StringUtils.isEmpty(logAuditEvent.propertyValue())) {
      try {
        return parseExpression(logAuditEvent.propertyValue(), String.class, joinPoint);
      } catch(Exception ex) {
        log.error("Could not evaluate property value expression {}", logAuditEvent.propertyValue());
      }
    }
    return null;
  }

  private Integer getObjectId(ProceedingJoinPoint joinPoint, LogAuditEvent logAuditEvent) {
    if (!StringUtils.isEmpty(logAuditEvent.objectId())) {
      try {
        return parseExpression(logAuditEvent.objectId(), Integer.class, joinPoint);
      } catch(Exception ex) {
        log.error("Could not evaluate object id expression {}", logAuditEvent.objectId());
      }
    }
    return null;
  }

  private String getObjectDetails(ProceedingJoinPoint joinPoint, LogAuditEvent logAuditEvent) {
    if (!StringUtils.isEmpty(logAuditEvent.objectDetails())) {
      try {
        return parseExpression(logAuditEvent.objectDetails(), String.class, joinPoint);
      } catch(Exception ex) {
        log.error("Could not evaluate object details expression {}", logAuditEvent.objectDetails());
      }
    }
    return null;
  }

  private Integer getParentRefId(ProceedingJoinPoint joinPoint, LogAuditEvent logAuditEvent) {
    if (!StringUtils.isEmpty(logAuditEvent.parentRefId())) {
      try {
        return parseExpression(logAuditEvent.parentRefId(), Integer.class, joinPoint);
      } catch(Exception ex) {
        log.error("Could not evaluate parentRefId id expression {}", logAuditEvent.parentRefId());
      }
    }
    return null;
  }

  private <T> T parseExpression(String expression, Class<T> targetClass, ProceedingJoinPoint joinPoint) {
    ExpressionParser parser = new SpelExpressionParser();
    StandardEvaluationContext context = new StandardEvaluationContext();
    CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
    if (Objects.nonNull(codeSignature)) {
      String[] parameterNames = codeSignature.getParameterNames();
      Object[] args = joinPoint.getArgs();

      for (int i = 0; i < parameterNames.length; i++) {
        context.setVariable(parameterNames[i], args[i]);
      }
    }
    Expression exp = parser.parseExpression(expression);
    return exp.getValue(context, targetClass);
  }
}
