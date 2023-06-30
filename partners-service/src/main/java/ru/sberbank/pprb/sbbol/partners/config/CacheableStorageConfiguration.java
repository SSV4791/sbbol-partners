package ru.sberbank.pprb.sbbol.partners.config;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sberbank.pprb.sbbol.partners.repository.partner.BudgetMaskDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.GkuInnDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.storage.BudgetMaskCacheableStorage;
import ru.sberbank.pprb.sbbol.partners.storage.GkuInnCacheableStorage;

import static ru.sberbank.pprb.sbbol.partners.storage.CacheNames.GET_BUDGET_MASKS;
import static ru.sberbank.pprb.sbbol.partners.storage.CacheNames.GET_BUDGET_MASKS_BY_TYPE;
import static ru.sberbank.pprb.sbbol.partners.storage.CacheNames.IS_GKU_INN;
import static ru.sberbank.pprb.sbbol.partners.storage.CacheNames.MIGRATION;

@Configuration
@EnableCaching
public class CacheableStorageConfiguration {

    @Value("${service.legacy.cache.time:300}")
    private int legacyCacheTime;

    @Value("${service.legacy.cache.size:1000}")
    private int legacyMaxEntriesLocalHeap;

    @Value("${service.gku_inn_dictionary.cache.time:3600}")
    private int gkuInnCacheTime;

    @Value("${service.gku_inn_dictionary.cache.size:10000}")
    private int gkuInnMaxEntriesLocalHeap;

    @Value("${service.budget_mask_dictionary.cache.size:10000}")
    private int budgetMaskMaxEntriesLocalHeap;

    @Bean("cacheableStorageCacheManager")
    public org.springframework.cache.CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }

    private net.sf.ehcache.CacheManager ehCacheManager() {
        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.addCache(createCacheConfig(MIGRATION, legacyMaxEntriesLocalHeap, legacyCacheTime));
        config.addCache(createCacheConfig(IS_GKU_INN, gkuInnMaxEntriesLocalHeap, gkuInnCacheTime));
        config.addCache(createCacheConfig(GET_BUDGET_MASKS, budgetMaskMaxEntriesLocalHeap));
        config.addCache(createCacheConfig(GET_BUDGET_MASKS_BY_TYPE, budgetMaskMaxEntriesLocalHeap));
        return net.sf.ehcache.CacheManager.create(config);
    }

    private CacheConfiguration createCacheConfig(String cacheName,  int maxEntriesLocalHeap) {
        return new CacheConfiguration(cacheName, maxEntriesLocalHeap)
            .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
            .eternal(true);
    }

    private CacheConfiguration createCacheConfig(String cacheName, int maxEntriesLocalHeap, int defaultCacheTime) {
        return new CacheConfiguration(cacheName, maxEntriesLocalHeap)
            .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
            .eternal(false)
            .timeToLiveSeconds(defaultCacheTime)
            .timeToIdleSeconds(defaultCacheTime);
    }

    @Bean
    GkuInnCacheableStorage gkuInnCacheableStorage(GkuInnDictionaryRepository gkuInnDictionaryRepository) {
        return new GkuInnCacheableStorage(gkuInnDictionaryRepository);
    }

    @Bean
    BudgetMaskCacheableStorage budgetMaskCacheableStorage(BudgetMaskDictionaryRepository budgetMaskDictionaryRepository) {
        return new BudgetMaskCacheableStorage(budgetMaskDictionaryRepository);
    }
}
