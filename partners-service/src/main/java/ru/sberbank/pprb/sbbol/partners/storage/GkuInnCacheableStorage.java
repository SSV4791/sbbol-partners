package ru.sberbank.pprb.sbbol.partners.storage;

import org.springframework.cache.annotation.Cacheable;
import ru.sberbank.pprb.sbbol.partners.repository.partner.GkuInnDictionaryRepository;

import java.util.Objects;

import static ru.sberbank.pprb.sbbol.partners.storage.CacheNames.IS_GKU_INN;

public class GkuInnCacheableStorage {

    private final GkuInnDictionaryRepository gkuInnDictionaryRepository;

    public GkuInnCacheableStorage(GkuInnDictionaryRepository gkuInnDictionaryRepository) {
        this.gkuInnDictionaryRepository = gkuInnDictionaryRepository;
    }

    @Cacheable(IS_GKU_INN)
    public boolean isGkuInn(String inn) {
        return Objects.nonNull(gkuInnDictionaryRepository.getByInn(inn));
    }
}
