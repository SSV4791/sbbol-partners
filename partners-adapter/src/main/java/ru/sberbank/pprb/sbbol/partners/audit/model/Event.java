package ru.sberbank.pprb.sbbol.partners.audit.model;

import java.util.Map;

public class Event {

    private EventType eventType;

    private Map<String, String> eventParams;

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Event eventType(EventType eventType) {
        this.eventType = eventType;
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
