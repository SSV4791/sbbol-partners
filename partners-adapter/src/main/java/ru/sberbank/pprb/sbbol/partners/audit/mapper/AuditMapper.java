package ru.sberbank.pprb.sbbol.partners.audit.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.audit.model.AuditEvent;
import ru.sberbank.pprb.sbbol.audit.model.AuditEventParams;
import ru.sberbank.pprb.sbbol.partners.audit.model.Event;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AuditMapper {

    @Mapping(target = "metamodelVersion", source = "params", qualifiedByName = "toMetamodelVersion")
    @Mapping(target = "name", source = "auditEvent.eventName")
    @Mapping(target = "module", source = "params", qualifiedByName = "toModelName")
    @Mapping(target = "createdAt", expression = "java(System.currentTimeMillis())")
    @Mapping(target = "userLogin", source = "params", qualifiedByName = "toModelName")
    @Mapping(target = "userNode", source = "params", qualifiedByName = "toUserNode")
    @Mapping(target = "params", source = "auditEvent.eventParams", qualifiedByName = "toAuditEventParams")
    AuditEvent toAuditEvent(Event auditEvent, Map<String, String> params);

    @Named("toMetamodelVersion")
    static String toMetamodelVersion(Map<String, String> params) {
        if (CollectionUtils.isEmpty(params)) {
            return null;
        }
        return params.get("metamodelVersion");
    }

    @Named("toModelName")
    static String toModelName(Map<String, String> params) {
        if (CollectionUtils.isEmpty(params)) {
            return null;
        }
        return params.get("moduleMame");
    }

    @Named("toUserNode")
    static String toUserNode(Map<String, String> params) {
        if (CollectionUtils.isEmpty(params)) {
            return null;
        }
        return params.get("userNode");
    }

    @Named("toAuditEventParams")
    static List<AuditEventParams> toAuditEventParams(Map<String, String> params) {
        if (CollectionUtils.isEmpty(params)) {
            return Collections.emptyList();
        }
        return params.entrySet().stream()
            .map(value -> new AuditEventParams().name(value.getKey()).value(value.getValue()))
            .collect(Collectors.toList());
    }
}

