package ru.sberbank.pprb.sbbol.partners.legacy.model;

import java.util.List;

/**
 * Обертка для списка с постраничной разбивкой
 */
public class ListResponse<CounterpartyView> {

    /**
     * Объект для организации постраничного просмотра
     */
    private Pagination pagination;

    /**
     * Список возвращаемых объектов
     */
    private List<CounterpartyView> items;

    public ListResponse() {
    }

    public ListResponse(List<CounterpartyView> items, Pagination pagination) {
        this.items = items;
        this.pagination = pagination;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public List<CounterpartyView> getItems() {
        return items;
    }

    public void setItems(List<CounterpartyView> items) {
        this.items = items;
    }
}
