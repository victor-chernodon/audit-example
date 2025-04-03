package com.auditexample.api.audit.common.domain.rqo;

import lombok.Getter;
import lombok.ToString;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2020-04-16<br/>
 */

@Getter
@ToString
public class GroupAuditEventsRQO extends CustomerAuditEventsRQO {

  private final Integer groupId;

  public GroupAuditEventsRQO(Integer customerId, Integer groupId, TimeMillisRange timeRange) {
    super(customerId, timeRange);
    this.groupId = groupId;
  }

}
