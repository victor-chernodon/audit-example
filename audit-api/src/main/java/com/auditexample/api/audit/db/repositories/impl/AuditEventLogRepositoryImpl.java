package com.auditexample.api.audit.db.repositories.impl;

import static com.auditexample.api.audit.db.DBMetaData.AUDIT_EVENTS_EMPLOYEE_ID_NAME;
import static com.auditexample.api.audit.db.DBMetaData.AUDIT_EVENTS_EVENT_TYPE_NAME;
import static com.auditexample.api.audit.db.DBMetaData.AUDIT_EVENTS_GROUP_ID_NAME;
import static com.auditexample.api.audit.db.DBMetaData.AUDIT_EVENTS_RANGE_KEY_NAME;
import static com.auditexample.api.audit.db.DBMetaData.RANDOM_SUFFIX_MAX;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.auditexample.api.audit.db.DBMetaData;
import com.auditexample.api.audit.db.entities.AuditEvent;
import com.auditexample.api.audit.db.entities.AuditEventId;
import com.auditexample.api.audit.db.repositories.AuditEventLogRepository;
import com.auditexample.api.audit.common.domain.pagination.PaginatedResult;
import com.auditexample.api.audit.common.domain.pagination.Pagination;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class AuditEventLogRepositoryImpl implements AuditEventLogRepository {

  private final DynamoDBMapperConfig mapperConfig;
  private final DynamoDBMapper mapper;

  public AuditEventLogRepositoryImpl(
      DynamoDBMapperConfig dynamoDBMapperConfig,
      DynamoDBMapper dynamoDBMapper) {
    this.mapperConfig = dynamoDBMapperConfig;
    this.mapper = dynamoDBMapper;
  }

  public void save(AuditEvent auditEvent) {
    mapper.save(auditEvent, mapperConfig);
  }

  @Override
  public PaginatedResult<List<AuditEvent>> loadEvents(Integer customerId, Long timestampFrom,
      Long timestampTo, int page, int pageSize) {
    var auditEventStream = IntStream.range(0, RANDOM_SUFFIX_MAX).boxed()
        .map(i -> findByCustomerId(customerId, i, timestampFrom, timestampTo))
        .flatMap(Collection::stream);
    var count = countEvents(customerId, timestampFrom, timestampTo);
    return paginate(auditEventStream, pageSize, page, count);
  }

  @Override
  public PaginatedResult<List<AuditEvent>> loadEventsByGroupIdAndEmployeeId(Integer customerId, Integer groupId,
      Long timestampFrom, Long timestampTo, int page, int pageSize) {
    var auditEventStream = IntStream.range(0, RANDOM_SUFFIX_MAX).boxed()
        .map(i -> findByCustomerIdAndGroupId(customerId, i, groupId, timestampFrom, timestampTo))
        .flatMap(Collection::stream);
    var count = countEvents(customerId, groupId, timestampFrom, timestampTo);
    return paginate(auditEventStream, pageSize, page, count);
  }

  @Override
  public PaginatedResult<List<AuditEvent>> loadEventsByGroupIdAndEventType(Integer customerId,
      Integer groupId, String eventType, Long timestampFrom, Long timestampTo, int page,
      int pageSize) {
    var auditEventStream = IntStream.range(0, RANDOM_SUFFIX_MAX).boxed()
        .map(i -> findByCustomerIdAndGroupIdAndEventType(customerId, i, groupId, eventType,
            timestampFrom, timestampTo))
        .flatMap(Collection::stream);
    var count = countEvents(customerId, groupId, eventType, timestampFrom, timestampTo);
    return paginate(auditEventStream, pageSize, page, count);
  }

  @Override
  public PaginatedResult<List<AuditEvent>> loadEventsByGroupIdAndEmployeeId(Integer customerId, Integer groupId,
      Integer employeeId, Long timestampFrom, Long timestampTo, int page, int pageSize) {
    var auditEventStream = IntStream.range(0, RANDOM_SUFFIX_MAX).boxed()
        .map(i -> findByCustomerIdAndGroupIdAndEmployeeId(customerId, i, groupId, employeeId,
            timestampFrom, timestampTo))
        .flatMap(Collection::stream);
    var count = countEvents(customerId, groupId, employeeId, timestampFrom, timestampTo);
    return paginate(auditEventStream, pageSize, page, count);
  }

  @Override
  public PaginatedResult<List<AuditEvent>> loadEventsByParentReference(Integer customerId,
      Integer parentRefId, Long timestampFrom, Long timestampTo, int page, int pageSize) {
    var auditEventStream = findByCustomerIdAndParentRefId(customerId, parentRefId,
        timestampFrom, timestampTo).stream();
    var count = countByCustomerIdAndParentRefId(customerId, parentRefId, timestampFrom, timestampTo);
    return paginate(auditEventStream, pageSize, page, count);
  }

  @Override
  public PaginatedResult<List<AuditEvent>> loadEventsByObjectId(Integer customerId,
      Integer objectId, Long timestampFrom, Long timestampTo, int page, int pageSize) {
    var auditEventStream = findByCustomerIdAndObjectId(customerId, objectId,
            timestampFrom, timestampTo).stream();
    var count = countByCustomerIdAndObjectId(customerId, objectId, timestampFrom, timestampTo);
    return paginate(auditEventStream, pageSize, page, count);
  }

  @Override
  public long countEvents(Integer customerId, Long timestampFrom, Long timestampTo) {
    return IntStream.range(0, RANDOM_SUFFIX_MAX).boxed()
        .mapToLong(i -> countByCustomerId(customerId, i, timestampFrom, timestampTo)).sum();
  }

  public long countEvents(Integer customerId, Integer groupId, Long timestampFrom, Long timestampTo) {
    return IntStream.range(0, RANDOM_SUFFIX_MAX).boxed()
        .mapToLong(i -> countByCustomerIdAndGroupId(customerId, groupId, i, timestampFrom, timestampTo)).sum();
  }

  public long countEvents(Integer customerId, Integer groupId, Integer employeeId,
      Long timestampFrom, Long timestampTo) {
    return IntStream.range(0, RANDOM_SUFFIX_MAX).boxed()
        .mapToLong(i -> countByCustomerIdAndGroupIdAndEmployeeId(customerId, groupId, employeeId, i,
            timestampFrom, timestampTo)).sum();
  }

  public long countEvents(Integer customerId, Integer groupId, String eventType, Long timestampFrom, Long timestampTo) {
    return IntStream.range(0, RANDOM_SUFFIX_MAX).boxed()
        .mapToLong(i -> countByCustomerIdAndGroupIdAndEventType(customerId, groupId, eventType, i,
            timestampFrom, timestampTo)).sum();
  }

  private DynamoDBQueryExpression<AuditEvent> createQueryByHashKey(int key, int keySuffix) {
    var auditEventId = new AuditEventId(key, keySuffix);
    AuditEvent entity = new AuditEvent();
    entity.setAuditEventId(auditEventId);
    return new DynamoDBQueryExpression<AuditEvent>()
        .withConsistentRead(true)
        .withHashKeyValues(entity);
  }

  private DynamoDBQueryExpression<AuditEvent> createQueryByHashKeyAndRange(int key,
      int keySuffix, Long rangeFrom, Long rangeTo) {
    Condition rangeKeyCondition = new Condition()
        .withComparisonOperator(ComparisonOperator.BETWEEN)
        .withAttributeValueList(
            new AttributeValue().withN(String.valueOf(rangeFrom)),
            new AttributeValue().withN(String.valueOf(rangeTo)));
    return createQueryByHashKey(key, keySuffix)
        .withRangeKeyCondition(AUDIT_EVENTS_RANGE_KEY_NAME, rangeKeyCondition);
  }

  private DynamoDBQueryExpression<AuditEvent> createQueryByCustomerIdAndObjectId(int customerId, int objectId) {
    AuditEvent entity = new AuditEvent();
    entity.setObjectIdHashKey(customerId, objectId);
    return new DynamoDBQueryExpression<AuditEvent>()
        .withConsistentRead(false)   // cannot use consistent read on GSI
        .withIndexName(DBMetaData.AUDIT_EVENTS_GSI_OBJECT_INDEX_NAME)
        .withHashKeyValues(entity);
  }

  private DynamoDBQueryExpression<AuditEvent> createQueryByCustomerIdAndParentRefId(int customerId, int parentRefId) {
    AuditEvent entity = new AuditEvent();
    entity.setParentRefIdHashKey(customerId, parentRefId);
    return new DynamoDBQueryExpression<AuditEvent>()
        .withConsistentRead(false)   // cannot use consistent read on GSI
        .withIndexName(DBMetaData.AUDIT_EVENTS_GSI_PARENT_REF_INDEX_NAME)
        .withHashKeyValues(entity);
  }

  private Condition createStringAttributeCondition(String value) {
    return new Condition().withComparisonOperator(ComparisonOperator.EQ)
        .withAttributeValueList(
            new AttributeValue().withS(value));
  }

  private Condition createNumericAttributeCondition(Number value) {
    return new Condition().withComparisonOperator(ComparisonOperator.EQ)
        .withAttributeValueList(
            new AttributeValue().withN(String.valueOf(value)));
  }

  private PaginatedQueryList<AuditEvent> findByCustomerId(Integer customerId, int suffix, long from, long to) {
    DynamoDBQueryExpression<AuditEvent> queryExpression = createQueryByHashKeyAndRange(customerId,
        suffix, from, to);
    return mapper.query(AuditEvent.class, queryExpression, mapperConfig);
  }

  private PaginatedQueryList<AuditEvent> findByCustomerIdAndGroupId(int customerId, int suffix, int groupId,
      long from, long to) {
    DynamoDBQueryExpression<AuditEvent> queryExpression = createQueryByHashKeyAndRange(customerId,
        suffix, from, to)
        .withQueryFilterEntry(AUDIT_EVENTS_GROUP_ID_NAME, createNumericAttributeCondition(groupId));
    return mapper.query(AuditEvent.class, queryExpression, mapperConfig);
  }

  private PaginatedQueryList<AuditEvent> findByCustomerIdAndGroupIdAndEventType(int customerId,
      int suffix, int groupId, String eventType, long from, long to) {
    DynamoDBQueryExpression<AuditEvent> queryExpression = createQueryByHashKeyAndRange(customerId,
        suffix, from, to)
        .withQueryFilter(
            Map.of(AUDIT_EVENTS_GROUP_ID_NAME, createNumericAttributeCondition(groupId),
                AUDIT_EVENTS_EVENT_TYPE_NAME, createStringAttributeCondition(eventType)));
    return mapper.query(AuditEvent.class, queryExpression, mapperConfig);
  }

  private PaginatedQueryList<AuditEvent> findByCustomerIdAndGroupIdAndEmployeeId(int customerId, int suffix,
      int groupId, int employeeId, long from, long to) {
    DynamoDBQueryExpression<AuditEvent> queryExpression = createQueryByHashKeyAndRange(customerId,
        suffix, from, to)
        .withQueryFilter(
            Map.of(AUDIT_EVENTS_GROUP_ID_NAME, createNumericAttributeCondition(groupId),
                AUDIT_EVENTS_EMPLOYEE_ID_NAME, createNumericAttributeCondition(employeeId)));
    return mapper.query(AuditEvent.class, queryExpression, mapperConfig);
  }

  private long countByCustomerId(Integer customerId, int suffix, long from, long to) {
    DynamoDBQueryExpression<AuditEvent> queryExpression = createQueryByHashKeyAndRange(customerId,
        suffix, from, to);
    return mapper.count(AuditEvent.class, queryExpression, mapperConfig);
  }

  private long countByCustomerIdAndGroupId(Integer customerId, Integer groupId, int suffix,
      long from, long to) {
    DynamoDBQueryExpression<AuditEvent> queryExpression = createQueryByHashKeyAndRange(customerId,
        suffix, from, to)
        .withQueryFilterEntry(AUDIT_EVENTS_GROUP_ID_NAME, createNumericAttributeCondition(groupId));
    return mapper.count(AuditEvent.class, queryExpression, mapperConfig);
  }

  private long countByCustomerIdAndGroupIdAndEmployeeId(Integer customerId, Integer groupId,
      Integer employeeId, int suffix, long from, long to) {
    DynamoDBQueryExpression<AuditEvent> queryExpression = createQueryByHashKeyAndRange(customerId,
        suffix, from, to)
        .withQueryFilter(
            Map.of(AUDIT_EVENTS_GROUP_ID_NAME, createNumericAttributeCondition(groupId),
                AUDIT_EVENTS_EMPLOYEE_ID_NAME, createNumericAttributeCondition(employeeId)));
    return mapper.count(AuditEvent.class, queryExpression, mapperConfig);
  }

  private long countByCustomerIdAndGroupIdAndEventType(Integer customerId, Integer groupId,
      String eventType, int suffix, long from, long to) {
    DynamoDBQueryExpression<AuditEvent> queryExpression = createQueryByHashKeyAndRange(customerId,
        suffix, from, to)
        .withQueryFilter(
            Map.of(AUDIT_EVENTS_GROUP_ID_NAME, createNumericAttributeCondition(groupId),
                AUDIT_EVENTS_EVENT_TYPE_NAME, createStringAttributeCondition(eventType)));
    return mapper.count(AuditEvent.class, queryExpression, mapperConfig);
  }

  private long countByCustomerIdAndObjectId(Integer customerId, Integer objectId, long from, long to) {
    Condition rangeKeyCondition = new Condition()
        .withComparisonOperator(ComparisonOperator.BETWEEN)
        .withAttributeValueList(
            new AttributeValue().withN(String.valueOf(from)),
            new AttributeValue().withN(String.valueOf(to)));
    DynamoDBQueryExpression<AuditEvent> queryExpression = createQueryByCustomerIdAndObjectId(customerId, objectId)
        .withRangeKeyCondition(AUDIT_EVENTS_RANGE_KEY_NAME, rangeKeyCondition);
    return mapper.count(AuditEvent.class, queryExpression, mapperConfig);
  }

  private PaginatedQueryList<AuditEvent> findByCustomerIdAndObjectId(Integer customerId, Integer objectId, long from, long to) {
    Condition rangeKeyCondition = new Condition()
        .withComparisonOperator(ComparisonOperator.BETWEEN)
        .withAttributeValueList(
            new AttributeValue().withN(String.valueOf(from)),
            new AttributeValue().withN(String.valueOf(to)));
    DynamoDBQueryExpression<AuditEvent> queryExpression = createQueryByCustomerIdAndObjectId(customerId, objectId)
        .withRangeKeyCondition(AUDIT_EVENTS_RANGE_KEY_NAME, rangeKeyCondition);
    return mapper.query(AuditEvent.class, queryExpression, mapperConfig);
  }

  private long countByCustomerIdAndParentRefId(Integer customerId, Integer parentRefId,
      long from, long to) {
    Condition rangeKeyCondition = new Condition()
        .withComparisonOperator(ComparisonOperator.BETWEEN)
        .withAttributeValueList(
            new AttributeValue().withN(String.valueOf(from)),
            new AttributeValue().withN(String.valueOf(to)));
    DynamoDBQueryExpression<AuditEvent> queryExpression = createQueryByCustomerIdAndParentRefId(customerId, parentRefId)
        .withRangeKeyCondition(AUDIT_EVENTS_RANGE_KEY_NAME, rangeKeyCondition);
    return mapper.count(AuditEvent.class, queryExpression, mapperConfig);
  }

  private PaginatedQueryList<AuditEvent> findByCustomerIdAndParentRefId(Integer customerId,
      Integer parentRefId, long from, long to) {
    Condition rangeKeyCondition = new Condition()
        .withComparisonOperator(ComparisonOperator.BETWEEN)
        .withAttributeValueList(
            new AttributeValue().withN(String.valueOf(from)),
            new AttributeValue().withN(String.valueOf(to)));
    DynamoDBQueryExpression<AuditEvent> queryExpression = createQueryByCustomerIdAndParentRefId(customerId, parentRefId)
        .withRangeKeyCondition(AUDIT_EVENTS_RANGE_KEY_NAME, rangeKeyCondition);
    return mapper.query(AuditEvent.class, queryExpression, mapperConfig);
  }

  private int numberOfPages(long totalNumberOfElements, int pageSize) {
    return (int) Math.ceil((double) totalNumberOfElements / pageSize);
  }

  private PaginatedResult<List<AuditEvent>> paginate(Stream<AuditEvent> auditEvents, int pageSize, int pageNumber, long total) {
    var result = auditEvents.distinct()
        .sorted(Comparator.comparing(AuditEvent::getTimestampUTC))
        .skip((pageNumber == 0 ? 0 : pageNumber - 1) * (long) pageSize)
        .limit(pageSize)
        .collect(Collectors.toList());
    return new PaginatedResult<>(result, new Pagination(total, pageNumber, numberOfPages(total, pageSize)));
  }
}
