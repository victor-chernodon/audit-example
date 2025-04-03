package com.auditexample.api.audit.services;

import com.auditexample.api.audit.common.domain.pagination.PaginatedResult;
import com.auditexample.api.audit.common.domain.rpo.AuditEventsRPO;
import com.auditexample.api.audit.common.domain.rqo.CustomerAuditEventsRQO;
import com.auditexample.api.audit.common.domain.rqo.EmployeeAuditEventsRQO;
import com.auditexample.api.audit.common.domain.rqo.GroupAuditEventsRQO;
import com.auditexample.api.audit.common.domain.rqo.ObjectAuditEventsRQO;
import com.auditexample.api.audit.common.domain.rqo.ParentRefAuditEventsRQO;
import com.auditexample.api.audit.common.services.AuditEventListService;
import com.auditexample.api.audit.services.legacy.CoreLogApiService;
import com.auditexample.api.audit.services.legacy.CoreLogEventType;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuditEventGateway {

  @Autowired
  private AuditEventListService auditEventListService;
  @Autowired(required = false)
  private CoreLogApiService coreLogApiService;

  public PaginatedResult<AuditEventsRPO> listEvents(CustomerAuditEventsRQO rqo, int page, int pageSize) {
    return auditEventListService.listEvents(rqo, page, pageSize);
  }

  public PaginatedResult<AuditEventsRPO> listEvents(GroupAuditEventsRQO rqo, int page, int pageSize) {
    return auditEventListService.listEvents(rqo, page, pageSize);
  }

  public PaginatedResult<AuditEventsRPO> listEvents(GroupAuditEventsRQO rqo, String eventType,
      int page, int pageSize) {
    if (coreLogShouldBeFetched.test(rqo)) {
      return coreLogApiService
          .listEvents(rqo.getCustomerId(), rqo.getGroupId(), CoreLogEventType.fromValue(eventType),
              rqo.getTimeRange(), page, pageSize);
    }
    return auditEventListService.listEvents(rqo, eventType, page, pageSize);
  }

  public PaginatedResult<AuditEventsRPO> listEvents(EmployeeAuditEventsRQO rqo, int page, int pageSize) {
    return auditEventListService.listEvents(rqo, page, pageSize);
  }

  public PaginatedResult<AuditEventsRPO> listEvents(ParentRefAuditEventsRQO rqo, int page, int pageSize) {
    return auditEventListService.listEvents(rqo, page, pageSize);
  }

  public PaginatedResult<AuditEventsRPO> listEvents(ObjectAuditEventsRQO rqo, int page, int pageSize) {
    return auditEventListService.listEvents(rqo, page, pageSize);
  }

  // TODO: add core log criteria
  private Predicate<GroupAuditEventsRQO> coreLogShouldBeFetched = groupAuditEventsRQO -> true;

}
