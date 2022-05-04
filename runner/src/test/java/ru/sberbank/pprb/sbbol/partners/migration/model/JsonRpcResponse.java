package ru.sberbank.pprb.sbbol.partners.migration.model;

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
