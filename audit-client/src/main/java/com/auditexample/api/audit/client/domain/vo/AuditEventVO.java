package com.auditexample.api.audit.client.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.auditexample.api.audit.client.domain.DomainConstants;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@JsonInclude(Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(
    ignoreUnknown = true
)
@Data
@SuperBuilder
@NoArgsConstructor
@ToString
public class AuditEventVO {

  @JsonProperty(value = DomainConstants.CUSTOMER_ID, required = true)
  private Integer customerId;

  @JsonProperty(DomainConstants.GROUP_ID)
  private Integer groupId;

  @JsonProperty(DomainConstants.EMPLOYEE_ID)
  private Integer employeeId;

  @JsonProperty(DomainConstants.EVENT)
  private AuditEventDetailsVO event;

}
