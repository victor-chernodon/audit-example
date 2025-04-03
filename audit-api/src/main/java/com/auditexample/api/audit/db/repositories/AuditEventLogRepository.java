package com.auditexample.api.audit.db.repositories;

import com.auditexample.api.audit.db.entities.AuditEvent;
import com.auditexample.api.audit.common.domain.pagination.PaginatedResult;
import java.util.List;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2020-04-28<br/>
 */

public interface AuditEventLogRepository {

  void save(AuditEvent auditEvent);

  PaginatedResult<List<AuditEvent>> loadEvents(Integer customerId, Long timestampFrom,
      Long timestampTo, int page, int pageSize);

  PaginatedResult<List<AuditEvent>> loadEventsByGroupIdAndEmployeeId(Integer customerId, Integer groupId,
      Long timestampFrom, Long timestampTo, int page, int pageSize);

  PaginatedResult<List<AuditEvent>> loadEventsByGroupIdAndEventType(Integer customerId, Integer groupId,
      String eventType, Long timestampFrom, Long timestampTo, int page, int pageSize);

  PaginatedResult<List<AuditEvent>> loadEventsByGroupIdAndEmployeeId(Integer customerId, Integer groupId,
      Integer employeeId, Long timestampFrom, Long timestampTo, int page, int pageSize);

  PaginatedResult<List<AuditEvent>> loadEventsByParentReference(Integer customerId, Integer parentRefId,
      Long timestampFrom, Long timestampTo, int page, int pageSize);

  PaginatedResult<List<AuditEvent>> loadEventsByObjectId(Integer customerId, Integer objectId,
      Long timestampFrom, Long timestampTo, int page, int pageSize);

  long countEvents(Integer customerId, Long timestampFrom, Long timestampTo);

}
