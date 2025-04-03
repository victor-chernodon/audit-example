package com.auditexample.api.audit.db.repositories;

import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.auditexample.api.audit.configs.CommonKafkaConfig;
import com.auditexample.api.audit.configs.KafkaConfig;
import com.auditexample.api.audit.db.DBMetaData;
import com.auditexample.api.audit.db.entities.AuditEvent;
import com.auditexample.api.audit.listeners.AuditEventListener;
import config.DynamoDBConfig;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.CollectionUtils;

@ExtendWith(SpringExtension.class)
@SpringBootTest({
    "core.audit.enable=false",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration",
    "dynamo.state.manager.enable=true",
    "dynamo.table.suffix=tests",
    "spring.cloud.consul.enabled=false"})
@DirtiesContext
@ContextConfiguration(classes = {DynamoDBConfig.class})
@MockBean({
    AuditEventListener.class, KafkaConfig.class, CommonKafkaConfig.class
})
class AuditEventLogRepositoryITest {

  @Autowired
  private AuditEventLogRepository auditEventLogRepository;

  private final int pageSize = 20;

  @Test
  void saveAndFetchEvent() {
    var timestamp = OffsetDateTime.now().minusMonths(1);
    var auditEvent = generateAuditEvent(timestamp);
    auditEventLogRepository.save(auditEvent);

    var fetchedAuditEvents = auditEventLogRepository
        .loadEvents(auditEvent.getCustomerId(), timestamp.toInstant().toEpochMilli(),
            timestamp.plusHours(1).toInstant().toEpochMilli(), 0, pageSize);
    assertFalse(CollectionUtils.isEmpty(fetchedAuditEvents.getResult()));
    assertEquals(1, fetchedAuditEvents.getPagination().getTotalCount().intValue());
    assertEquals(auditEvent.getEventType(), fetchedAuditEvents.getResult().get(0).getEventType());
    assertEquals(auditEvent.getCustomerId(), fetchedAuditEvents.getResult().get(0).getCustomerId());
    assertEquals(auditEvent.getTimestampUTC(), fetchedAuditEvents.getResult().get(0).getTimestampUTC());
    assertEquals(auditEvent.getEmployeeId(), fetchedAuditEvents.getResult().get(0).getEmployeeId());
    assertEquals(auditEvent.getGroupId(), fetchedAuditEvents.getResult().get(0).getGroupId());
    assertEquals(auditEvent.getEventDetails(), fetchedAuditEvents.getResult().get(0).getEventDetails());
    assertEquals(auditEvent.getTimestamp(), fetchedAuditEvents.getResult().get(0).getTimestamp());
    assertEquals(auditEvent.getObjectDetails(), fetchedAuditEvents.getResult().get(0).getObjectDetails());
    assertEquals(auditEvent.getPropertyName(), fetchedAuditEvents.getResult().get(0).getPropertyName());
    assertEquals(auditEvent.getPropertyValue(), fetchedAuditEvents.getResult().get(0).getPropertyValue());
    assertEquals(auditEvent.getParentRefId(), fetchedAuditEvents.getResult().get(0).getParentRefId());
    assertEquals(auditEvent.getObjectId(), fetchedAuditEvents.getResult().get(0).getObjectId());
  }

  @Test
  void saveAndFetchEventsByCustomerGroupAndEmployeeIds() {
    var timestamp = OffsetDateTime.now().minusMonths(2);
    var auditEvent = generateAuditEvent(timestamp);
    var employeeId = auditEvent.getEmployeeId();
    auditEventLogRepository.save(auditEvent);
    auditEvent.setTimestampUTC(auditEvent.getTimestampUTC() + 1);
    auditEventLogRepository.save(auditEvent);

    auditEvent.setTimestampUTC(auditEvent.getTimestampUTC() + 1);
    auditEvent.setEmployeeId(nextInt());
    auditEventLogRepository.save(auditEvent);
    var anotherAuditEvent = generateAuditEvent();
    auditEventLogRepository.save(anotherAuditEvent);

    var fetchedAuditEvents = auditEventLogRepository
        .loadEventsByGroupIdAndEmployeeId(auditEvent.getCustomerId(), auditEvent.getGroupId(), employeeId,
            timestamp.toInstant().toEpochMilli(),
            timestamp.plusMinutes(1).toInstant().toEpochMilli(), 1, pageSize);
    assertEquals(2, fetchedAuditEvents.getPagination().getTotalCount().intValue());
  }

  @Test
  void saveAndFetchEventsByCustomerAndGroupId() {
    var timestamp = OffsetDateTime.now().minusMonths(3);
    var auditEvent = generateAuditEvent(timestamp);
    var groupId = auditEvent.getGroupId();
    auditEventLogRepository.save(auditEvent);
    auditEvent.setTimestampUTC(auditEvent.getTimestampUTC() + 1);
    auditEventLogRepository.save(auditEvent);

    auditEvent.setTimestampUTC(auditEvent.getTimestampUTC() + 1);
    auditEvent.setGroupId(nextInt());
    auditEventLogRepository.save(auditEvent);
    var anotherAuditEvent = generateAuditEvent();
    auditEventLogRepository.save(anotherAuditEvent);

    var fetchedAuditEvents = auditEventLogRepository
        .loadEventsByGroupIdAndEmployeeId(auditEvent.getCustomerId(), groupId,
            timestamp.toInstant().toEpochMilli(),
            timestamp.plusMinutes(1).toInstant().toEpochMilli(), 1, pageSize);
    assertEquals(2, fetchedAuditEvents.getPagination().getTotalCount().intValue());
  }

  @Test
  void saveAndFetchEventsByCustomerAndGroupIdAndEventType() {
    var timestamp = OffsetDateTime.now().minusMonths(3);
    var eventType = "Type1";
    var auditEvent = generateAuditEvent(timestamp);
    var groupId = auditEvent.getGroupId();
    auditEventLogRepository.save(auditEvent);
    auditEvent.setEventType(eventType);
    auditEvent.setTimestampUTC(auditEvent.getTimestampUTC() + 1);
    auditEventLogRepository.save(auditEvent);

    auditEvent.setTimestampUTC(auditEvent.getTimestampUTC() + 1);
    auditEventLogRepository.save(auditEvent);

    auditEvent.setTimestampUTC(auditEvent.getTimestampUTC() + 1);
    auditEvent.setGroupId(nextInt());
    auditEventLogRepository.save(auditEvent);
    var anotherAuditEvent = generateAuditEvent();
    auditEventLogRepository.save(anotherAuditEvent);

    var fetchedAuditEvents = auditEventLogRepository
        .loadEventsByGroupIdAndEventType(auditEvent.getCustomerId(), groupId, eventType,
            timestamp.toInstant().toEpochMilli(),
            timestamp.plusMinutes(1).toInstant().toEpochMilli(), 1, pageSize);
    assertEquals(2, fetchedAuditEvents.getPagination().getTotalCount().intValue());
    assertTrue(fetchedAuditEvents.getResult().stream().allMatch(event -> eventType.equals(event.getEventType())));
    assertTrue(fetchedAuditEvents.getResult().stream().allMatch(event -> groupId.equals(event.getGroupId())));
  }

  @Test
  void saveAndFetchEventsByCustomerAndObjectId() {
    var timestamp = OffsetDateTime.now().minusMonths(3);
    var auditEvent = generateAuditEvent(timestamp);
    var objectId = auditEvent.getObjectId();
    auditEventLogRepository.save(auditEvent);
    auditEvent.setTimestampUTC(auditEvent.getTimestampUTC() + 1);
    auditEventLogRepository.save(auditEvent);

    auditEvent.setTimestampUTC(auditEvent.getTimestampUTC() + 1);
    auditEvent.setObjectIdHashKey(auditEvent.getCustomerId(), nextInt());
    auditEventLogRepository.save(auditEvent);
    var anotherAuditEvent = generateAuditEvent();
    auditEventLogRepository.save(anotherAuditEvent);

    var fetchedAuditEvents = auditEventLogRepository
        .loadEventsByObjectId(auditEvent.getCustomerId(), objectId,
            timestamp.toInstant().toEpochMilli(),
            timestamp.plusMinutes(1).toInstant().toEpochMilli(), 1, pageSize);
    assertEquals(2, fetchedAuditEvents.getPagination().getTotalCount().intValue());
  }

  @Test
  void saveAndFetchEventsByCustomerAndParentRefId() {
    var timestamp = OffsetDateTime.now().minusMonths(3);
    var auditEvent = generateAuditEvent(timestamp);
    var parentRefId = nextInt();
    auditEvent.setParentRefIdHashKey(auditEvent.getCustomerId(), parentRefId);
    auditEventLogRepository.save(auditEvent);
    auditEvent.setTimestampUTC(auditEvent.getTimestampUTC() + 1);
    auditEventLogRepository.save(auditEvent);

    auditEvent.setTimestampUTC(auditEvent.getTimestampUTC() + 1);
    auditEvent.setParentRefIdHashKey(auditEvent.getCustomerId(), nextInt());
    auditEventLogRepository.save(auditEvent);
    var anotherAuditEvent = generateAuditEvent();
    auditEventLogRepository.save(anotherAuditEvent);

    var fetchedAuditEvents = auditEventLogRepository
        .loadEventsByParentReference(auditEvent.getCustomerId(), parentRefId,
            timestamp.toInstant().toEpochMilli(),
            timestamp.plusMinutes(1).toInstant().toEpochMilli(), 1, pageSize);
    assertEquals(2, fetchedAuditEvents.getPagination().getTotalCount().intValue());
  }

  @Test
  void shouldBeNoRecordsWithDuplicatedId() {
    var customerId = nextInt();
    var timestamp = OffsetDateTime.now().minusMonths(4);

    var auditEventHashKey = customerId + DBMetaData.AUDIT_EVENTS_HASH_KEY_SEPARATOR
        + nextInt(0, DBMetaData.RANDOM_SUFFIX_MAX);
    var auditEvent1 = generateAuditEvent(timestamp);
    auditEvent1.setAuditEventKey(auditEventHashKey);
    var auditEvent2 = generateAuditEvent(timestamp);
    auditEvent2.setAuditEventKey(auditEventHashKey);

    auditEventLogRepository.save(auditEvent1);
    var countBeforeSecondInsert = auditEventLogRepository
        .countEvents(customerId, timestamp.toInstant().toEpochMilli(),
            timestamp.toInstant().toEpochMilli());

    auditEventLogRepository.save(auditEvent2);

    var actualAuditEntries = auditEventLogRepository
        .loadEvents(customerId, timestamp.minusHours(1).toInstant().toEpochMilli(),
            timestamp.plusHours(1).toInstant().toEpochMilli(), 0, pageSize);
    assertEquals(countBeforeSecondInsert, actualAuditEntries.getPagination().getTotalCount().intValue());
    assertEquals(1, actualAuditEntries.getPagination().getTotalCount().intValue());
    assertEquals(auditEvent2.getGroupId(), actualAuditEntries.getResult().get(0).getGroupId());
  }

  @Test
  void shouldAddAnotherRecordsIfTimestampInKeyDiffers() {
    var customerId = nextInt();
    var timestamp = OffsetDateTime.now().minusMonths(5);

    var auditEventHashKey = customerId + DBMetaData.AUDIT_EVENTS_HASH_KEY_SEPARATOR
        + nextInt(0, DBMetaData.RANDOM_SUFFIX_MAX);
    var auditEvent = generateAuditEvent(timestamp);
    auditEvent.setAuditEventKey(auditEventHashKey);

    auditEventLogRepository.save(auditEvent);
    var countBeforeSecondInsert = auditEventLogRepository
        .loadEvents(customerId, timestamp.toInstant().toEpochMilli(),
            timestamp.toInstant().toEpochMilli(), 0, pageSize).getPagination().getTotalCount();

    auditEvent.setTimestampUTC(auditEvent.getTimestampUTC() + 1);
    auditEventLogRepository.save(auditEvent);

    var actualAuditEntries = auditEventLogRepository
        .loadEvents(customerId, timestamp.toInstant().toEpochMilli(),
            timestamp.plusMinutes(1).toInstant().toEpochMilli(), 0, pageSize);
    assertNotEquals(countBeforeSecondInsert, actualAuditEntries.getPagination().getTotalCount());
    assertEquals(2, actualAuditEntries.getPagination().getTotalCount().intValue());
    assertNotEquals(actualAuditEntries.getResult().get(0).getTimestampUTC(),
        actualAuditEntries.getResult().get(1).getTimestampUTC());
  }

  @Disabled("Takes too much time")
  @Test
  void testPagination() {
    int numberOfRecordsToCreate = 300000;
    int customerId = nextInt();
    var events = IntStream.range(0, numberOfRecordsToCreate).boxed()
        .map(i -> {
          var event = generateAuditEvent(customerId);
          event.setTimestampUTC((long) i);
          return event;
        }).collect(Collectors.toList());
    events.forEach(auditEventLogRepository::save);

    var firstPage = auditEventLogRepository
        .loadEvents(customerId, 0L, (long) numberOfRecordsToCreate, 0, pageSize);
    assertNotNull(firstPage);
    assertEquals(numberOfRecordsToCreate, firstPage.getPagination().getTotalCount().longValue());
    assertEquals((long) Math.ceil((double) numberOfRecordsToCreate / pageSize),
        firstPage.getPagination().getTotalPages().longValue());
    assertEquals(0, firstPage.getPagination().getCurrentPage().intValue());
    assertEquals(pageSize, firstPage.getResult().size());
    assertEquals(0, firstPage.getResult().get(0).getTimestampUTC().longValue());
    assertEquals(pageSize - 1, firstPage.getResult().get(pageSize - 1).getTimestampUTC().longValue());

    var secondPage = auditEventLogRepository
        .loadEvents(customerId, 0L, 3000000L, 1, pageSize);
    assertNotNull(firstPage);
    assertEquals(numberOfRecordsToCreate, secondPage.getPagination().getTotalCount().longValue());
    assertEquals(numberOfRecordsToCreate / pageSize, secondPage.getPagination().getTotalPages().longValue());
    assertEquals(1, secondPage.getPagination().getCurrentPage().intValue());
    assertEquals(pageSize, secondPage.getResult().size());
    assertNotEquals(firstPage.getResult(), secondPage.getResult());
    assertEquals(pageSize, secondPage.getResult().get(0).getTimestampUTC().longValue());
    assertEquals(pageSize * 2 - 1, secondPage.getResult().get(pageSize - 1).getTimestampUTC().longValue());
  }

  private static AuditEvent generateAuditEvent() {
    var timestamp = OffsetDateTime.now().minusSeconds(nextLong(0, 5000));
    return generateAuditEvent(timestamp);
  }

  private static AuditEvent generateAuditEvent(OffsetDateTime timestamp) {
    var customerId = nextInt();
    var eventType = "eventType-" + UUID.randomUUID().toString();
    var groupId = nextInt();
    var employeeId = nextInt();
    var eventDetails = "eventDetails-" + UUID.randomUUID().toString();
    var objectId = nextInt();
    var parentRefId = nextBoolean() ? nextInt() : null;
    var objectDetails = nextBoolean() ? RandomStringUtils.randomAlphanumeric(30) : null;
    var propertyName = "propertyName-" + UUID.randomUUID().toString();
    var propertyValue = "propertyValue-" + UUID.randomUUID().toString();
    return new AuditEvent(customerId, timestamp, eventType, groupId, employeeId, objectId,
        parentRefId, eventDetails, objectDetails, propertyName, propertyValue);
  }

  private static AuditEvent generateAuditEvent(Integer customerId) {
    var groupId = nextInt();
    var eventType = "eventType-" + UUID.randomUUID().toString();
    var employeeId = nextInt();
    var timestamp = OffsetDateTime.now();
    var eventDetails = "eventDetails-" + UUID.randomUUID().toString();
    var objectDetails = nextBoolean() ? RandomStringUtils.randomAlphanumeric(30) : null;
    var propertyName = "propertyName-" + UUID.randomUUID().toString();
    var propertyValue = "propertyValue-" + UUID.randomUUID().toString();
    return new AuditEvent(customerId, timestamp, eventType, groupId, employeeId, eventDetails,
        objectDetails, propertyName, propertyValue);
  }

  private static List<AuditEvent> generateAuditEvents(Integer customerId, int amount) {
    return IntStream.range(0, amount).boxed().map(i -> generateAuditEvent(customerId))
        .collect(Collectors.toList());
  }
}
