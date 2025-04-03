package com.auditexample.api.audit.common.domain.rpo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.auditexample.api.audit.common.domain.DomainConstants;
import com.auditexample.api.audit.common.domain.vo.AuditEventVO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2020-04-16<br/>
 */
@JsonIgnoreProperties(
    ignoreUnknown = true
)
@JsonInclude(Include.NON_DEFAULT)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEventsRPO {

  @JsonProperty(DomainConstants.EVENTS)
  private List<AuditEventVO> auditEvents;

}
