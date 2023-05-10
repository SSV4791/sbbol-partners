package ru.sberbank.pprb.sbbol.partners.service.systeminfo;


public interface SystemInfoService {

    /**
     * Получение режима работы БД
     * @return Информация о статусе БД
     */
    SystemInfo systemInfo();
}
