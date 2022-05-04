package ru.sberbank.pprb.sbbol.partners.legacy.model;

import java.util.Objects;

/**
 * Нумерация
 */
public class Pagination {

    /**
     * Смещение от начала
     */
    private Integer offset;

    /**
     * Количество возвращаемых записей
     */
    private Integer count;

    /**
     * Признак наличия следующей страницы
     */
    private Boolean hasNextPage;

    public Pagination() {
    }

    public Pagination(Integer offset, Integer count) {
        this.offset = offset;
        this.count = count;
    }

    public Pagination(Integer offset, Integer count, boolean hasNextPage) {
        this.offset = offset;
        this.count = count;
        this.hasNextPage = hasNextPage;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Boolean getHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(Boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pagination)) {
            return false;
        }
        Pagination that = (Pagination) o;
        return Objects.equals(offset, that.offset) &&
            Objects.equals(count, that.count) &&
            Objects.equals(hasNextPage, that.hasNextPage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset, count, hasNextPage);
    }
}
