package ru.sberbank.pprb.sbbol.partners.migration.model;

// TODO DCBBRAIN-2268 Перенести тесты и модели для migration-service из модуля Runner в свой модуль
/**
 * Ответ в формате json-rpc
 */
public class JsonRpcResponse<T> extends JsonRpcEntity {

    /**
     * Результат запроса
     */
    private final T result;

    JsonRpcResponse(String id, String jsonRpc, T result) {
        super(id, jsonRpc);
        this.result = result;
    }

    public T getResult() {
        return result;
    }
}
