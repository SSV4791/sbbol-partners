package ru.sberbank.pprb.sbbol.migration.correspondents.service;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationCorrespondentCandidate;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigratedCorrespondentData;

import java.util.List;

/**
 * Сервис миграции контрагентов
 */
@JsonRpcService("/correspondents/migrate")
public interface CorrespondentMigrationService {

    /**
     * Мигрировать контрагентов
     *
     * @param digitalId идентификатор организации
     * @param correspondents список контрагнетов
     * @return список идентификаторов мигрированных контрагентов
     */
    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidFormatException.class, code = -32600),
        @JsonRpcError(exception = InvalidTypeIdException.class, code = -32600),
    })
    List<MigratedCorrespondentData> migrate(@JsonRpcParam(value = "digitalId") String digitalId,
                                            @JsonRpcParam(value = "correspondents") List<MigrationCorrespondentCandidate> correspondents);

}
