package ru.sberbank.pprb.sbbol.partners.migration.model;

// TODO DCBBRAIN-2268 Перенести тесты и модели для migration-service из модуля Runner в свой модуль
/**
 * Запрос в формате json-rpc
 */
public class JsonRpcRequest<T> extends JsonRpcEntity {

    /**
     * Наименование вызываемого удаленного метода
     */
    private String method;

    /**
     * Параметры-тело запроса
     */
    private T params;

    public JsonRpcRequest(String id, String jsonrpc, String method, T params) {
        super(id, jsonrpc);
        this.method = method;
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public T getParams() {
        return params;
    }

    public void setParams(T params) {
        this.params = params;
    }
}
