package ru.sberbank.pprb.sbbol.partners.legacy.config;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@EnableCaching
@Configuration
public class CacheConfig {

    @Value("${service.legacy.housing.inns.cache.time}")
    private static int housingInnsCacheTime;

    @Value("${service.legacy.default.cache.time}")
    private static int defaultCacheTime;

    @Value("${service.legacy.max.entries.local.heap}")
    private static int maxEntriesLocalHeap;

    private static final Map<String, Integer> serviceCacheMap = Map.of(
        "housingInns", housingInnsCacheTime
    );

    @Bean("legacySbbolAdapterCacheManager")
    public org.springframework.cache.CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }

    private net.sf.ehcache.CacheManager ehCacheManager() {
        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.addCache(createCacheConfig("housingInns"));
        config.addCache(createCacheConfig("migration"));
        return net.sf.ehcache.CacheManager.create(config);
    }

    private CacheConfiguration createCacheConfig(String cacheName) {
        Integer cacheTime = serviceCacheMap.get(cacheName);
        return new CacheConfiguration(cacheName, maxEntriesLocalHeap)
            .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
            .eternal(false)
            .timeToLiveSeconds(cacheTime != null ? cacheTime : defaultCacheTime)
            .timeToIdleSeconds(cacheTime != null ? cacheTime : defaultCacheTime);
    }
}
