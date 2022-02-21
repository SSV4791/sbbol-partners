package ru.sberbank.pprb.sbbol.migration.correspondents.enums;

/**
 * Признак подписанности.
 */
public enum MigrationAccountStateType {

    SIGNED("Подписан"),
    NOT_SIGNED("Не подписан"),
    ;

    private final String desc;

    MigrationAccountStateType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static MigrationAccountStateType of(Boolean isSigned) {
        if (isSigned != null) {
            return isSigned ? SIGNED : NOT_SIGNED;
        }
        return NOT_SIGNED;
    }
}
