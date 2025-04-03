package com.auditexample.api.audit.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UaaResponse {

  @JsonProperty("access_token")
  private String token;
  @JsonProperty("token_type")
  private String tokenType;
  @JsonProperty("expires_in")
  private Integer expiresIn;
  @JsonProperty("scope")
  private String scope;
  @JsonProperty("jti")
  private String jti;

}
