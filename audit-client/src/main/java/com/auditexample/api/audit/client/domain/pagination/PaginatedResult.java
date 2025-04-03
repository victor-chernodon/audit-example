package com.auditexample.api.audit.client.domain.pagination;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class PaginatedResult<T> {

  private final T result;
  private final Pagination pagination;

  @JsonCreator
  public PaginatedResult(@JsonProperty("result") T result,
      @JsonProperty("pagination") Pagination pagination) {
    this.result = result;
    this.pagination = pagination;
  }
}
