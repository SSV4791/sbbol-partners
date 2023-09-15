package ru.sberbank.pprb.sbbol.partners.service.fraud;

import ru.sberbank.pprb.sbbol.partners.entity.partner.BaseEntity;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;
import ru.sberbank.pprb.sbbol.partners.model.fraud.FraudEventType;

import javax.validation.constraints.NotNull;

public interface FraudService<T extends BaseEntity> {

    FraudEventType getEventType();

    void sendEvent(FraudMetaData metaData, T entity);

    //TODO Временное решение до перехода на новое взаимодействие с FROUD
    //TODO убрать при реализации https://jira.sberbank.ru/browse/DCBBRAIN-5205
    default boolean checkEvent(@NotNull FraudMetaData metaData) {
        var channelInfo = metaData.getChannelInfo();
        if (channelInfo != null) {
            var clientDefinedChannelIndicator = channelInfo.getClientDefinedChannelIndicator();
            if (clientDefinedChannelIndicator != null) {
                return switch (clientDefinedChannelIndicator) {
                    case UPG_1C, UPG_SBB, UPG_CORP -> false;
                    default -> true;
                };
            }
        }
        return true;
    }
}
