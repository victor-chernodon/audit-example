package com.auditexample.api.audit.db.entities;

import com.auditexample.api.audit.db.DBMetaData;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@Setter
@ToString
@EqualsAndHashCode
class AuditEventHashKeyWithPrefix {

  private final Integer prefix;
  private final Integer key;

  public AuditEventHashKeyWithPrefix(int prefix, int key) {
    this.prefix = prefix;
    this.key = key;
  }

  public AuditEventHashKeyWithPrefix(String hashKeyWithPrefix) {
    var keyParts = hashKeyWithPrefix.split(DBMetaData.AUDIT_EVENTS_HASH_KEY_SEPARATOR);
    this.prefix = Integer.valueOf(keyParts[0]);
    this.key = Integer.valueOf(keyParts[1]);
  }

  public String getAuditEventHashKeyWithPrefix() {
    return buildKey(prefix, key);
  }

  private static String buildKey(int prefix, int key) {
    return prefix + DBMetaData.AUDIT_EVENTS_HASH_KEY_SEPARATOR + key;
  }

  public Integer getKey() {
    return key;
  }
}
