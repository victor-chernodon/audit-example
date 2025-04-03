package com.auditexample.api.audit.client.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import com.auditexample.api.audit.client.domain.DomainConstants;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2020-04-16<br/>
 */

@Data
@SuperBuilder
@NoArgsConstructor
@ToString(exclude = "eventDetails")
@JsonPropertyOrder({
    DomainConstants.EVENT_TYPE,
    DomainConstants.EVENT_DETAILS,
    DomainConstants.EVENT_OBJECT_ID,
    DomainConstants.EVENT_PARENT_REFERENCE_ID,
    DomainConstants.EVENT_OBJECT_DETAILS,
    DomainConstants.EVENT_PROPERTY_NAME,
    DomainConstants.EVENT_PROPERTY_VALUE,
    DomainConstants.EVENT_TIMESTAMP
})
public class AuditEventDetailsVO {

  @JsonProperty(value = DomainConstants.EVENT_TYPE, required = true)
  private String eventType;

  @JsonProperty(value = DomainConstants.EVENT_TIMESTAMP, required = true)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DomainConstants.TIMESTAMP_ISO_8601_ZONE_OFFSET_PATTERN)
  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  private OffsetDateTime timestamp;

  @JsonProperty(DomainConstants.EVENT_DETAILS)
  private String eventDetails;

  @JsonProperty(value = DomainConstants.EVENT_OBJECT_ID)
  private Integer objectId;

  @JsonProperty(value = DomainConstants.EVENT_PARENT_REFERENCE_ID)
  private Integer parentRefId;

  @JsonProperty(DomainConstants.EVENT_OBJECT_DETAILS)
  private String objectDetails;

  @JsonProperty(value = DomainConstants.EVENT_PROPERTY_NAME)
  private String propertyName;

  @JsonProperty(value = DomainConstants.EVENT_PROPERTY_VALUE)
  private String propertyValue;

}
