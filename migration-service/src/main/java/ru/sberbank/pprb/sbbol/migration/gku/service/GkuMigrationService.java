package ru.sberbank.pprb.sbbol.migration.gku.service;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;
import ru.sberbank.pprb.sbbol.migration.gku.model.MigrationGkuCandidate;

import java.time.LocalDate;
import java.util.List;

@JsonRpcService("/gku/migrate")
public interface GkuMigrationService {

    /**
     * Мигрировать справочник gku
     *
     * @param gkuInns список ИНН
     */
    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidFormatException.class, code = -32600),
        @JsonRpcError(exception = InvalidTypeIdException.class, code = -32600),
    })
    void migrate(@JsonRpcParam(value = "gkuInns") List<MigrationGkuCandidate> gkuInns);

    /**
     * Очистка справочника
     */
    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidFormatException.class, code = -32600),
        @JsonRpcError(exception = InvalidTypeIdException.class, code = -32600),
    })
    void delete(@JsonRpcParam(value = "migrateDate") LocalDate migrateDate);
}
