package ru.sberbank.pprb.sbbol.partners.legacy.config;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CacheConfig {

    @Value("${service.legacy.default.cache.time}")
    private int defaultCacheTime;

    @Value("${service.legacy.max.entries.local.heap}")
    private int maxEntriesLocalHeap;

    @Bean("legacySbbolAdapterCacheManager")
    public org.springframework.cache.CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }

    private net.sf.ehcache.CacheManager ehCacheManager() {
        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.addCache(createCacheConfig("migration"));
        return net.sf.ehcache.CacheManager.create(config);
    }

    private CacheConfiguration createCacheConfig(String cacheName) {
        return new CacheConfiguration(cacheName, maxEntriesLocalHeap)
            .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
            .eternal(false)
            .timeToLiveSeconds(defaultCacheTime)
            .timeToIdleSeconds(defaultCacheTime);
    }
}
