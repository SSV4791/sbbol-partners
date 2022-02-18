package ru.sberbank.pprb.sbbol.partners.migration.model;

import java.io.Serializable;

// TODO DCBBRAIN-2268 Перенести тесты и модели для migration-service из модуля Runner в свой модуль
/**
 * Сущность для формирования запроса/ответа при json-rpc взаимодействии
 */
public class JsonRpcEntity implements Serializable {

    /**
     * Идентификатор
     */
    private String id;

    /**
     * Версия протокола
     */
    private String jsonrpc;

    JsonRpcEntity(String id, String jsonRpc) {
        this.id = id;
        this.jsonrpc = jsonRpc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }
}
