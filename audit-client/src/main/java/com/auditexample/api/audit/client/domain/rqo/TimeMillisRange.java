package com.auditexample.api.audit.client.domain.rqo;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Objects;

public class TimeMillisRange {

  private final Long begin;
  private final Long end;

  private TimeMillisRange(Long begin, Long end) {
    this.begin = begin;
    this.end = end;
  }

  public static TimeMillisRange of(OffsetDateTime begin, OffsetDateTime end) {
    Objects.requireNonNull(begin, "begin");
    Objects.requireNonNull(end, "end");
    return new TimeMillisRange(begin.toInstant().toEpochMilli(), end.toInstant().toEpochMilli());
  }

  public static TimeMillisRange of(Instant begin, Instant end) {
    Objects.requireNonNull(begin, "begin");
    Objects.requireNonNull(end, "end");
    return new TimeMillisRange(begin.toEpochMilli(), end.toEpochMilli());
  }

  public static TimeMillisRange of(Long begin, Long end) {
    Objects.requireNonNull(begin, "begin");
    Objects.requireNonNull(end, "end");
    return new TimeMillisRange(begin, end);
  }

  public Long getBegin() {
    return begin;
  }

  public Long getEnd() {
    return end;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TimeMillisRange that = (TimeMillisRange) o;
    return Objects.equals(begin, that.begin) &&
        Objects.equals(end, that.end);
  }

  @Override
  public int hashCode() {
    return Objects.hash(begin, end);
  }

}
