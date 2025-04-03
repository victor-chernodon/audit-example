package com.auditexample.api.audit.db.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import java.time.OffsetDateTime;

public class OffsetDateTimeConverter implements DynamoDBTypeConverter<String, OffsetDateTime> {

  @Override
  public String convert(final OffsetDateTime time) {
    return time.toString();
  }

  @Override
  public OffsetDateTime unconvert(final String time) {
    return OffsetDateTime.parse(time);
  }
}
