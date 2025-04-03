package com.auditexample.api.audit.domain.core.pagination;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class PaginatedCoreResult<T> {

  private final T logItems;
  private final Long totalCount;
  private final Integer pageNumber;
  private final Integer totalPages;
  private final Integer itemsPerPage;

  @JsonCreator
  public PaginatedCoreResult(@JsonProperty("logItems") T logItems,
      @JsonProperty("totalCount") long totalCount,
      @JsonProperty("pageNumber") int pageNumber,
      @JsonProperty("totalPages") int totalPages,
      @JsonProperty("itemsPerPage") int itemsPerPage) {
    this.logItems = logItems;
    this.totalCount = totalCount;
    this.pageNumber = pageNumber;
    this.totalPages = totalPages;
    this.itemsPerPage = itemsPerPage;
  }
}
