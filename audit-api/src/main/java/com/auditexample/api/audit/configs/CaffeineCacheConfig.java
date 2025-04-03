package com.auditexample.api.audit.configs;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
@Slf4j
public class CaffeineCacheConfig {

  private static final int INITIAL_CAPACITY = 100;
  private static final int MAXIMUM_SIZE = 500;

  public static class FiveMinutesCache {

    public static final String UNIT_ID_BY_GROUP_ID = "unit-id-by-group-id";
    public static final String TIMEZONE_BY_GROUP = "timezone-by-group";

    static List<String> getNames() {
      try {
        List<String> names = new ArrayList<>();

        for (Field declaredField : FiveMinutesCache.class.getFields()) {
          String value = (String) declaredField.get(null);
          names.add(value);
        }
        return names;
      } catch (IllegalAccessException e) {
        return Collections.emptyList();
      }
    }

    private FiveMinutesCache() {
      // Utility
    }
  }

  @Bean
  @ConditionalOnProperty(name = "caching.enable", havingValue = "true")
  public CacheManager caffeineCacheManager() {
    SimpleCacheManager manager = new SimpleCacheManager();
    manager.setCaches(buildCaches());
    return manager;
  }

  private List<Cache> buildCaches() {
    List<Cache> caches = new ArrayList<>();
    for (String name : FiveMinutesCache.getNames()) {
      caches.add(buildCache(name, 5));
    }
    return caches;
  }

  private Cache buildCache(String name, int ttlInMinutes) {
    return buildCache(name, ttlInMinutes, MAXIMUM_SIZE);
  }

  private Cache buildCache(String name, int ttlInMinutes, int maxSize) {
    return new CaffeineCache(name, Caffeine.newBuilder()
        .initialCapacity(INITIAL_CAPACITY)
        .maximumSize(maxSize)
        .expireAfterWrite(ttlInMinutes, TimeUnit.MINUTES)
        .recordStats()
        .build());
  }

  @Bean
  @Qualifier("caffeineCacheManager")
  @ConditionalOnMissingBean
  public CacheManager decafNoOpCacheManager() {
    log.info("Caffeine caching disabled!");
    return new NoOpCacheManager();
  }
}
