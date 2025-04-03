package com.auditexample.api.audit.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2020-05-12<br/>
 */
@AllArgsConstructor
@Getter
public class KafkaService {

  private String bootstrapServers;

}
