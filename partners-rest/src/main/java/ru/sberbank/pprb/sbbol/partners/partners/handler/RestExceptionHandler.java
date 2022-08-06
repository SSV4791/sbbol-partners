package ru.sberbank.pprb.sbbol.partners.partners.handler;

import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.orm.hibernate5.HibernateOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.BadRequestException;
import ru.sberbank.pprb.sbbol.partners.exception.CheckValidationException;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.EntrySaveException;
import ru.sberbank.pprb.sbbol.partners.exception.OptimisticLockException;
import ru.sberbank.pprb.sbbol.partners.exception.PartnerMigrationException;
import ru.sberbank.pprb.sbbol.partners.model.Descriptions;
import ru.sberbank.pprb.sbbol.partners.model.Error;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionHandler.class);
    private static final String FILL_OBJECT_MESSAGE_EXCEPTION = "Ошибка заполнения объекта";

    @ExceptionHandler({
        DataIntegrityViolationException.class
    })
    protected ResponseEntity<?> handleConstraintViolationException(
        DataIntegrityViolationException ex,
        HttpServletRequest httpRequest
    ) {
        LOG.error("Нарушение ограничений уникальности в БД", ex);
        return buildResponseEntity(
            HttpStatus.BAD_REQUEST,
            "PPRB:PARTNER:MODEL_DUPLICATE_EXCEPTION",
            MessagesTranslator.toLocale("error.message.check.validation"),
            httpRequest.getRequestURL()
        );
    }

    @ExceptionHandler({
        EntryNotFoundException.class,
        EntityNotFoundException.class,
        ObjectNotFoundException.class,
        PartnerMigrationException.class
    })
    protected ResponseEntity<Object> handleObjectNotFoundException(
        Exception ex,
        HttpServletRequest httpRequest
    ) {
        LOG.error("Объект не найден", ex);
        return buildResponseEntity(
            HttpStatus.NOT_FOUND,
            ex.getLocalizedMessage(),
            httpRequest.getRequestURL()
        );
    }

    @ExceptionHandler(OptimisticLockException.class)
    protected ResponseEntity<Object> handleObjectModelValidationException(
        OptimisticLockException ex,
        HttpServletRequest httpRequest
    ) {
        LOG.error(FILL_OBJECT_MESSAGE_EXCEPTION, ex);
        return buildResponsesEntity(
            HttpStatus.BAD_REQUEST.name(),
            ex.getErrors(),
            ex.getText(),
            httpRequest.getRequestURL()
        );
    }

    @ExceptionHandler(CheckValidationException.class)
    protected ResponseEntity<Object> handleObjectModelValidationException(
        CheckValidationException ex,
        HttpServletRequest httpRequest
    ) {
        LOG.error(FILL_OBJECT_MESSAGE_EXCEPTION, ex);
        return buildResponsesEntity(
            ex.getText(),
            ex.getErrors(),
            ex.getText(),
            httpRequest.getRequestURL()
        );
    }

    @ExceptionHandler({
        BadRequestException.class,
        EntrySaveException.class,
        OptimisticLockingFailureException.class
    })
    protected ResponseEntity<Object> handleObjectBadRequestException(
        Exception ex,
        HttpServletRequest httpRequest
    ) {
        LOG.error(FILL_OBJECT_MESSAGE_EXCEPTION, ex);
        return buildResponseEntity(
            HttpStatus.BAD_REQUEST,
            ex.getLocalizedMessage(),
            httpRequest.getRequestURL()
        );
    }


    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleException(
        Exception ex,
        HttpServletRequest httpRequest
    ) {
        LOG.error("Необработанное исключение", ex);
        return buildResponseEntity(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ex.getLocalizedMessage(),
            httpRequest.getRequestURL()
        );
    }

    @ExceptionHandler(HibernateOptimisticLockingFailureException.class)
    protected ResponseEntity<Object> handleObjectBadRequestExceptionHibernate(
        HibernateOptimisticLockingFailureException ex,
        HttpServletRequest httpRequest) {
        LOG.error("Версия записи в базе данных не равна версии в запросе", ex);
        return buildResponseEntity(
            HttpStatus.BAD_REQUEST, "Версия записи в базе данных не равна версии в запросе: "
                + ex.getLocalizedMessage(),
            httpRequest.getRequestURL()
        );
    }

    @NotNull
    @Override
    protected @NonNull ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatus status,
        @NonNull WebRequest request
    ) {
        var errorsField = ex.getFieldErrors().stream()
            .collect(groupingBy(FieldError::getField,
                collectingAndThen(
                    toList(), list -> list.stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(toList())
                )
            ));
        var errorsGlobal = ex.getGlobalErrors().stream()
            .collect(groupingBy(ObjectError::getObjectName,
                    collectingAndThen(
                        toList(), list -> list.stream()
                            .map(ObjectError::getDefaultMessage)
                            .collect(toList()))

                )
            );
        var errors =
            Stream.concat(errorsField.entrySet().stream(), errorsGlobal.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return buildResponsesEntity(
            "PPRB:PARTNER:MODEL_VALIDATION_EXCEPTION",
            errors,
            MessagesTranslator.toLocale("error.message.check.validation"),
            ((ServletWebRequest) request).getRequest().getRequestURL()
        );
    }

    @NotNull
    @Override
    protected @NonNull
    ResponseEntity<Object> handleExceptionInternal(
        Exception ex,
        Object body,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatus status,
        @NonNull WebRequest request
    ) {
        return buildResponseEntity(
            status,
            ex.getLocalizedMessage(),
            ((ServletWebRequest) request).getRequest().getRequestURL()
        );
    }

    private ResponseEntity<Object> buildResponseEntity(
        HttpStatus status,
        String errorDesc,
        StringBuffer requestUrl
    ) {
        var errorData = new Error().code(status.name()).text(Collections.singletonList(errorDesc));
        String url = requestUrl.toString().replaceAll("[\n\r\t]", "_");
        LOG.error("Ошибка вызова \"{}\": {}", url, errorData);
        return new ResponseEntity<>(
            errorData,
            status
        );
    }

    private ResponseEntity<Object> buildResponseEntity(
        HttpStatus status,
        String errorCode,
        String errorDesc,
        StringBuffer requestUrl
    ) {
        var errorData = new Error().code(errorCode).text(Collections.singletonList(errorDesc));
        String url = requestUrl.toString().replaceAll("[\n\r\t]", "_");
        LOG.error("Ошибка вызова \"{}\": {}", url, errorData);
        return new ResponseEntity<>(
            errorData,
            status
        );
    }

    private ResponseEntity<Object> buildResponsesEntity(
        String errorCode,
        Map<String, List<String>> errors,
        String text,
        StringBuffer requestUrl
    ) {
        var descriptions = errors.entrySet().stream()
            .map(value -> new Descriptions().field(value.getKey()).message(value.getValue()))
            .collect(toList());
        var errorData = new Error()
            .code(errorCode)
            .descriptions(descriptions)
            .text(Collections.singletonList(text));
        String url = requestUrl.toString().replaceAll("[\n\r\t]", "_");
        LOG.error("Ошибка вызова \"{}\": {}", url, errorData);
        return new ResponseEntity<>(
            errorData,
            HttpStatus.BAD_REQUEST
        );
    }
}
