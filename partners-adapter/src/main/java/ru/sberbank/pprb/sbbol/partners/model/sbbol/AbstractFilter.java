package ru.sberbank.pprb.sbbol.partners.model.sbbol;

import java.io.Serializable;

/**
 * Абстрактрый класс для парамеров фильтрации
 */
public abstract class AbstractFilter implements Serializable {

    /**
     * Постраничная разбивка
     */
    private Pagination pagination;

    /**
     * Сортировка
     */
    private String orderBy;

    public AbstractFilter() {
    }

    public AbstractFilter(Pagination pagination) {
        this.pagination = pagination;
    }

    public Pagination getPagination() {
        return pagination == null ? new Pagination(0, 0) : pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public String toString() {
        return "AbstractFilter{" +
            "pagination=" + pagination +
            ", orderBy='" + orderBy + '\'' +
            '}';
    }
}