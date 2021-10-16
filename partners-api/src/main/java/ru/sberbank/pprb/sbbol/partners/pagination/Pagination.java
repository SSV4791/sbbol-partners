package ru.sberbank.pprb.sbbol.partners.pagination;

import java.util.Objects;

/**
 * Пагинация
 */
public final class Pagination {

    /**
     * Смещение от начала
     */
    public final Integer offset;

    /**
     * Количество возвращаемых записей
     */
    public final Integer count;

    /**
     * Признак наличия следующей страницы
     */
    public final Boolean hasNextPage;

    public Pagination(
        Integer offset,
        Integer count,
        Boolean hasNextPage
    ) {

        this.offset = offset;
        this.count = count;
        this.hasNextPage = hasNextPage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pagination other = (Pagination) o;
        return Objects.equals(offset, other.offset) &&
            Objects.equals(count, other.count) &&
            Objects.equals(hasNextPage, other.hasNextPage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            offset,
            count,
            hasNextPage
        );
    }

    @Override
    public String toString() {
        return "Pagination{" +
            "offset='" + offset + '\'' +
            ", count='" + count + '\'' +
            ", hasNextPage='" + hasNextPage + '\'' +
            '}';
    }
}
