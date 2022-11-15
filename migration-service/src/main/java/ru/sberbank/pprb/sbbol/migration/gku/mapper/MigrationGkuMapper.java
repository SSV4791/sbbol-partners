package ru.sberbank.pprb.sbbol.migration.gku.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.sberbank.pprb.sbbol.migration.gku.entity.MigrationGkuInnEntity;
import ru.sberbank.pprb.sbbol.migration.gku.model.MigrationGkuCandidate;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MigrationGkuMapper {

    List<MigrationGkuInnEntity> toDictionary(List<MigrationGkuCandidate> candidate);

    @Mapping(target = "inn", source = "candidate.inn")
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    MigrationGkuInnEntity toDictionary(MigrationGkuCandidate candidate);
}
