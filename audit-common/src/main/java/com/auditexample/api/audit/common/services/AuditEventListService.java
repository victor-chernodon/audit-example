package com.auditexample.api.audit.common.services;

import com.auditexample.api.audit.common.domain.rpo.AuditEventsRPO;
import com.auditexample.api.audit.common.domain.rqo.CustomerAuditEventsRQO;
import com.auditexample.api.audit.common.domain.rqo.EmployeeAuditEventsRQO;
import com.auditexample.api.audit.common.domain.rqo.GroupAuditEventsRQO;
import com.auditexample.api.audit.common.domain.rqo.ObjectAuditEventsRQO;
import com.auditexample.api.audit.common.domain.rqo.ParentRefAuditEventsRQO;
import com.auditexample.api.audit.common.domain.pagination.PaginatedResult;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2020-04-15<br/>
 */

public interface AuditEventListService {

  PaginatedResult<AuditEventsRPO> listEvents(CustomerAuditEventsRQO rqo, int page, int pageSize);

  PaginatedResult<AuditEventsRPO> listEvents(GroupAuditEventsRQO rqo, int page, int pageSize);

  PaginatedResult<AuditEventsRPO> listEvents(GroupAuditEventsRQO rqo, String eventType, int page, int pageSize);

  PaginatedResult<AuditEventsRPO> listEvents(EmployeeAuditEventsRQO rqo, int page, int pageSize);

  PaginatedResult<AuditEventsRPO> listEvents(ParentRefAuditEventsRQO rqo, int page, int pageSize);

  PaginatedResult<AuditEventsRPO> listEvents(ObjectAuditEventsRQO rqo, int page, int pageSize);

}
