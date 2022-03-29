package ru.sberbank.pprb.sbbol.migration.gku.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.sberbank.pprb.sbbol.migration.gku.entity.MigrationGkuInnEntity;
import ru.sberbank.pprb.sbbol.migration.gku.model.MigrationGkuCandidate;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MigrationGkuMapper {

    @Mapping(target = "inn", source = "candidate.inn")
    List<MigrationGkuInnEntity> toDictionary(List<MigrationGkuCandidate> candidate);
}
