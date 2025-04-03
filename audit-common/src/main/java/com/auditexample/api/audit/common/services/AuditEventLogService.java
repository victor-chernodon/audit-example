package com.auditexample.api.audit.common.services;

import com.auditexample.api.audit.common.domain.vo.AuditEventVO;

/**
 * Created at Auditexample<br/> User: Vladimir Gromov<br/> Date: 2020-07-14<br/>
 */

public interface AuditEventLogService {

  void logEvent(AuditEventVO auditEventVO);

}
