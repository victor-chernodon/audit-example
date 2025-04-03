package com.auditexample.api.audit.common.domain.rqo;

import lombok.Getter;
import lombok.ToString;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2020-04-16<br/>
 */

@Getter
@ToString
public class CustomerAuditEventsRQO extends AuditEventsRQO {

  private final Integer customerId;

  public CustomerAuditEventsRQO(Integer customerId, TimeMillisRange timeRange) {
    super(timeRange);
    this.customerId = customerId;
  }

}
