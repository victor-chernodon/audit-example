package com.auditexample.api.audit.client.domain.pagination;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Pagination {

  private final Long totalCount;
  private final Integer currentPage;
  private final Integer totalPages;

  @JsonCreator
  public Pagination(@JsonProperty("totalCount") long totalCount,
      @JsonProperty("currentPage") int currentPage,
      @JsonProperty("totalPage") int totalPages) {
    this.totalCount = totalCount;
    this.currentPage = currentPage;
    this.totalPages = totalPages;
  }
}