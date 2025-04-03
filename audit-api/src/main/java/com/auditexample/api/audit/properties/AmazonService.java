package com.auditexample.api.audit.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2019-09-12<br/>
 */
@Component
@ConfigurationProperties(prefix = "amazon.aws")
@Getter
@Setter
public class AmazonService {

  private String accesskey = "none";
  private String secretkey = "none";
  private String region = "eu-central-1";

}
