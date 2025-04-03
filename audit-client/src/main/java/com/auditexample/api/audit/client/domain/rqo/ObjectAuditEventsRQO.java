package com.auditexample.api.audit.client.domain.rqo;

import lombok.Getter;
import lombok.ToString;

/**
 * Created at Auditexample<br/> User: Vladimir Gromov<br/> Date: 2020-07-14<br/>
 */

@Getter
@ToString
public class ObjectAuditEventsRQO extends CustomerAuditEventsRQO {

  private final Integer objectId;

  public ObjectAuditEventsRQO(Integer customerId, Integer objectId, TimeMillisRange timeRange) {
    super(customerId, timeRange);
    this.objectId = objectId;
  }

}
