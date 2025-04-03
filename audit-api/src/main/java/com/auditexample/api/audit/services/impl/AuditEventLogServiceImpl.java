package com.auditexample.api.audit.services.impl;

import com.auditexample.api.audit.common.domain.vo.AuditEventVO;
import com.auditexample.api.audit.common.services.AuditEventLogService;
import com.auditexample.api.audit.db.repositories.AuditEventLogRepository;
import com.auditexample.api.audit.services.AuditEventMappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created at Auditexample<br/> User: Vladimir Gromov<br/> Date: 2020-07-14<br/>
 */

@Service
@Slf4j
public class AuditEventLogServiceImpl implements AuditEventLogService {

  @Autowired
  private AuditEventLogRepository auditEventLogRepository;

  @Override
  public void logEvent(AuditEventVO auditEventVO) {
    auditEventLogRepository.save(
        AuditEventMappers.mapToAuditEvent(auditEventVO));
    log.debug("logged event: {}", auditEventVO);
  }

}
