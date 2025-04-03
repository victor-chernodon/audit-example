package com.auditexample.api.audit.client.domain.rqo;

import lombok.Getter;
import lombok.ToString;

/**
 * Created at Auditexample<br/> User: Vladimir Gromov<br/> Date: 2020-07-14<br/>
 */

@Getter
@ToString
public class ParentRefAuditEventsRQO extends CustomerAuditEventsRQO {

  private final Integer parentRefId;

  public ParentRefAuditEventsRQO(Integer customerId, Integer parentRefId, TimeMillisRange timeRange) {
    super(customerId, timeRange);
    this.parentRefId = parentRefId;
  }

}
