package ru.sberbank.pprb.sbbol.migration.correspondents.service;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationCorrespondentCandidate;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationCorrespondentResponse;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationReplicationGuidCandidate;

import java.util.List;

/**
 * Сервис миграции контрагентов
 */
@JsonRpcService("/correspondents/migrate")
public interface CorrespondentMigrationService {

    /**
     * Мигрировать контрагентов
     *
     * @param digitalId      идентификатор организации
     * @param correspondents список контрагнетов
     * @return список идентификаторов мигрированных контрагентов
     */
    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidFormatException.class, code = -32600),
        @JsonRpcError(exception = InvalidTypeIdException.class, code = -32600),
    })
    MigrationCorrespondentResponse migrate(
        @JsonRpcParam(value = "digitalId") String digitalId,
        @JsonRpcParam(value = "correspondents") List<MigrationCorrespondentCandidate> correspondents
    );

    /**
     * Сохранение контрагентов при вызове из Legacy
     *
     * @param digitalId     идентификатор организации
     * @param correspondent контрагент
     * @return сохраненный контрагент
     */
    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidFormatException.class, code = -32600),
        @JsonRpcError(exception = InvalidTypeIdException.class, code = -32600),
    })
    MigrationCorrespondentCandidate save(
        @JsonRpcParam(value = "digitalId") String digitalId,
        @JsonRpcParam(value = "correspondent") MigrationCorrespondentCandidate correspondent
    );

    /**
     * Удаление контрагентов при вызове из Legacy
     *
     * @param digitalId идентификатор организации
     * @param pprbGuid  идентификатор счета
     */
    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidFormatException.class, code = -32600),
        @JsonRpcError(exception = InvalidTypeIdException.class, code = -32600),
    })
    void delete(
        @JsonRpcParam(value = "digitalId") String digitalId,
        @JsonRpcParam(value = "pprbGuid") String pprbGuid
    );

    /**
     * Миграция репликационных guid контрагентов для поддержания вызовов из УПШ
     *
     * @param digitalId  идентификатор организации
     * @param candidates кандидат на миграцию
     */
    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidFormatException.class, code = -32600),
        @JsonRpcError(exception = InvalidTypeIdException.class, code = -32600),
    })
    void migrateReplicationGuid(
        @JsonRpcParam(value = "digitalId") String digitalId,
        @JsonRpcParam(value = "candidates") List<MigrationReplicationGuidCandidate> candidates
    );
}
