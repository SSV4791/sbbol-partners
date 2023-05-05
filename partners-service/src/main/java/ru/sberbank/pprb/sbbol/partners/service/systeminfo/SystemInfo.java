package ru.sberbank.pprb.sbbol.partners.service.systeminfo;

public class SystemInfo {

    private String dataSourceMode;

    public SystemInfo() {

    }

    public SystemInfo(String dataSourceMode) {
        this.dataSourceMode = dataSourceMode;
    }

    public String getDataSourceMode() {
        return dataSourceMode;
    }

    public void setDataSourceMode(String dataSourceMode) {
        this.dataSourceMode = dataSourceMode;
    }
}
