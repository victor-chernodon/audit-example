package com.auditexample.api.audit.client.publishers;

import com.auditexample.api.audit.client.domain.vo.AuditEventVO;

public interface AuditEventsPublisher {
  void publish(AuditEventVO event, String tokenValue);
}
