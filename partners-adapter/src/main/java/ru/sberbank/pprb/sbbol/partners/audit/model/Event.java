package ru.sberbank.pprb.sbbol.partners.audit.model;

import java.util.Map;

public class Event {

    private String eventName;

    private Map<String, String> eventParams;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Event eventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public Map<String, String> getEventParams() {
        return eventParams;
    }

    public void setEventParams(Map<String, String> eventParams) {
        this.eventParams = eventParams;
    }

    public Event eventParams(Map<String, String> eventParams) {
        this.eventParams = eventParams;
        return this;
    }
}
