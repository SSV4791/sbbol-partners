package ru.sberbank.pprb.sbbol.partners;

/**
 * Неуспешное прохождение проверки
 */
public class CheckFailure {

    /**
     * Сообщение об ошибке
     */
    private String msg;

    /**
     * Поле в котором содержится ошибка
     */
    private String field;

    public CheckFailure(String msg, String field) {
        this.msg = msg;
        this.field = field;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
