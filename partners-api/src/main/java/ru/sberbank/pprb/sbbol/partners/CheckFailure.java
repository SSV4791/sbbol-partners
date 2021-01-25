package ru.sberbank.pprb.sbbol.partners;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * Неуспешное прохождение проверки
 */
@Getter
@Setter
@AllArgsConstructor
public class CheckFailure {

    /**
     * Сообщение об ошибке
     */
    private String msg;

    /**
     * Поле в котором содержится ошибка
     */
    private String field;

}
