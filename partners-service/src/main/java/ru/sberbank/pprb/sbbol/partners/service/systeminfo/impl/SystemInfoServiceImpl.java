package ru.sberbank.pprb.sbbol.partners.service.systeminfo.impl;

import ru.sberbank.pprb.sbbol.partners.service.systeminfo.SystemInfo;
import ru.sberbank.pprb.sbbol.partners.service.systeminfo.SystemInfoService;
import ru.sbrf.journal.standin.ResourceNotAllowedException;
import ru.sbrf.journal.standin.StandinResourceHelper;

public class SystemInfoServiceImpl implements SystemInfoService {

    private final StandinResourceHelper<String> standinResourceHelper;

    public SystemInfoServiceImpl(StandinResourceHelper<String> standinResourceHelper) {
        this.standinResourceHelper = standinResourceHelper;
    }

    public SystemInfo systemInfo() {
        String mode;
        try {
            mode = standinResourceHelper.getResource();
        } catch (ResourceNotAllowedException e) {
            // в случае STOP кидается ResourceNotAllowedException
            mode = "stop";
        }
        return new SystemInfo(mode);
    }
}
