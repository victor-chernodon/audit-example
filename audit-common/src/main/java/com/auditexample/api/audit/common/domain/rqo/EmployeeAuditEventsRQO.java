package com.auditexample.api.audit.common.domain.rqo;

import lombok.Getter;
import lombok.ToString;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2020-04-16<br/>
 */

@Getter
@ToString
public class EmployeeAuditEventsRQO extends GroupAuditEventsRQO {

  private final Integer employeeId;

  public EmployeeAuditEventsRQO(Integer customerId, Integer groupId, Integer employeeId, TimeMillisRange timeRange) {
    super(customerId, groupId, timeRange);
    this.employeeId = employeeId;
  }

}
