package com.auditexample.api.audit.services.impl;

import com.auditexample.api.audit.common.domain.rqo.ObjectAuditEventsRQO;
import com.auditexample.api.audit.common.domain.rqo.ParentRefAuditEventsRQO;
import com.auditexample.api.audit.common.services.AuditEventListService;
import com.auditexample.api.audit.db.repositories.AuditEventLogRepository;
import com.auditexample.api.audit.common.domain.pagination.PaginatedResult;
import com.auditexample.api.audit.common.domain.rpo.AuditEventsRPO;
import com.auditexample.api.audit.common.domain.rqo.CustomerAuditEventsRQO;
import com.auditexample.api.audit.common.domain.rqo.EmployeeAuditEventsRQO;
import com.auditexample.api.audit.common.domain.rqo.GroupAuditEventsRQO;
import com.auditexample.api.audit.services.AuditEventMappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2020-04-15<br/>
 */

@Service
@Slf4j
public class AuditEventServiceImpl implements AuditEventListService {

  @Autowired
  private AuditEventLogRepository auditEventLogRepository;

  @Override
  public PaginatedResult<AuditEventsRPO> listEvents(CustomerAuditEventsRQO rqo, int page, int pageSize) {
    var auditEvents = auditEventLogRepository.loadEvents(
        rqo.getCustomerId(),
        rqo.getTimeRange().getBegin(),
        rqo.getTimeRange().getEnd(),
        page,
        pageSize
    );
    log.debug("{} events loaded", auditEvents.getPagination().getCurrentPage());
    return AuditEventMappers.mapToPaginatedCustomerAuditEventsRPO(auditEvents);
  }

  @Override
  public PaginatedResult<AuditEventsRPO> listEvents(GroupAuditEventsRQO rqo, int page, int pageSize) {
    var auditEvents = auditEventLogRepository.loadEventsByGroupIdAndEmployeeId(
        rqo.getCustomerId(),
        rqo.getGroupId(),
        rqo.getTimeRange().getBegin(),
        rqo.getTimeRange().getEnd(),
        page,
        pageSize
    );
    log.debug("{} events loaded", auditEvents.getPagination().getCurrentPage());
    return AuditEventMappers.mapToPaginatedGroupAuditEventsRPO(auditEvents);
  }

  @Override
  public PaginatedResult<AuditEventsRPO> listEvents(GroupAuditEventsRQO rqo, String eventType, int page, int pageSize) {
    var auditEvents = auditEventLogRepository.loadEventsByGroupIdAndEventType(
        rqo.getCustomerId(),
        rqo.getGroupId(),
        eventType,
        rqo.getTimeRange().getBegin(),
        rqo.getTimeRange().getEnd(),
        page,
        pageSize
    );
    log.debug("{} events loaded", auditEvents.getPagination().getCurrentPage());
    return AuditEventMappers.mapToPaginatedGroupAuditEventsRPO(auditEvents);
  }

  @Override
  public PaginatedResult<AuditEventsRPO> listEvents(EmployeeAuditEventsRQO rqo, int page, int pageSize) {
    var auditEvents = auditEventLogRepository.loadEventsByGroupIdAndEmployeeId(
        rqo.getCustomerId(),
        rqo.getGroupId(),
        rqo.getEmployeeId(),
        rqo.getTimeRange().getBegin(),
        rqo.getTimeRange().getEnd(),
        page,
        pageSize
    );
    log.debug("{} events loaded", auditEvents.getPagination().getCurrentPage());
    return AuditEventMappers.mapToPaginatedEmployeeAuditEventsRPO(auditEvents);
  }

  @Override
  public PaginatedResult<AuditEventsRPO> listEvents(ParentRefAuditEventsRQO rqo,
      int page, int pageSize) {
    var auditEvents = auditEventLogRepository.loadEventsByGroupIdAndEmployeeId(
        rqo.getCustomerId(),
        rqo.getParentRefId(),
        rqo.getTimeRange().getBegin(),
        rqo.getTimeRange().getEnd(),
        page,
        pageSize
    );
    log.debug("{} events loaded", auditEvents.getPagination().getCurrentPage());
    return AuditEventMappers.mapToPaginatedGroupAuditEventsRPO(auditEvents);
  }

  @Override
  public PaginatedResult<AuditEventsRPO> listEvents(ObjectAuditEventsRQO rqo, int page, int pageSize) {
    var auditEvents = auditEventLogRepository.loadEventsByGroupIdAndEmployeeId(
        rqo.getCustomerId(),
        rqo.getObjectId(),
        rqo.getTimeRange().getBegin(),
        rqo.getTimeRange().getEnd(),
        page,
        pageSize
    );
    log.debug("{} events loaded", auditEvents.getPagination().getCurrentPage());
    return AuditEventMappers.mapToPaginatedGroupAuditEventsRPO(auditEvents);
  }

}
