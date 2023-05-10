package ru.sberbank.pprb.sbbol.partners.systeminfo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.service.systeminfo.SystemInfo;
import ru.sberbank.pprb.sbbol.partners.service.systeminfo.SystemInfoService;

@RestController
@RequestMapping("/system-info")
public class SystemInfoController {

    private final SystemInfoService systemInfoService;

    public SystemInfoController(SystemInfoService systemInfoService) {
        this.systemInfoService = systemInfoService;
    }

    @GetMapping
    public SystemInfo systemInfo() {
        return systemInfoService.systemInfo();
    }
}
