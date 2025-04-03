package com.auditexample.api.audit.client.domain.rqo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2020-04-16<br/>
 */

@Getter
@AllArgsConstructor
@ToString
public class AuditEventsRQO {

  private final TimeMillisRange timeRange;

}
