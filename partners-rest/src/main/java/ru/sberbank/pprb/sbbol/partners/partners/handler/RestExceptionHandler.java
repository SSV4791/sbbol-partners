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
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.AccountAlreadySignedException;
import ru.sberbank.pprb.sbbol.partners.exception.AccountPriorityOneMoreException;
import ru.sberbank.pprb.sbbol.partners.exception.CheckValidationException;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.EntrySaveException;
import ru.sberbank.pprb.sbbol.partners.exception.FraudDeniedException;
import ru.sberbank.pprb.sbbol.partners.exception.FraudModelValidationException;
import ru.sberbank.pprb.sbbol.partners.exception.IllegalArgumentIdsHistoryException;
import ru.sberbank.pprb.sbbol.partners.exception.MultipleEntryFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.OptimisticLockException;
import ru.sberbank.pprb.sbbol.partners.exception.PartnerMigrationException;
import ru.sberbank.pprb.sbbol.partners.exception.common.BaseException;
import ru.sberbank.pprb.sbbol.partners.model.Descriptions;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.ENTRY_SAVE_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.FRAUD_DENIED_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.FRAUD_MODEL_VALIDATION_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_DUPLICATE_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_NOT_FOUND_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_VALIDATION_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MULTIPLE_FOUND_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.model.Error.TypeEnum.BUSINESS;
import static ru.sberbank.pprb.sbbol.partners.model.Error.TypeEnum.CRITICAL;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionHandler.class);

    private static final String CHECK_VALIDATION_MESSAGE = "error.message.check.validation";

    @ExceptionHandler({
        DataIntegrityViolationException.class
    })
    protected ResponseEntity<?> handleConstraintViolationException(
        DataIntegrityViolationException ex,
        HttpServletRequest httpRequest
    ) {
        LOG.error("Нарушение ограничений уникальности в БД", ex);
        return buildResponsesEntity(
            HttpStatus.BAD_REQUEST,
            BUSINESS,
            MODEL_DUPLICATE_EXCEPTION.getValue(),
            MessagesTranslator.toLocale(CHECK_VALIDATION_MESSAGE),
            Collections.emptyMap(),
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
        LOG.warn("Объект не найден", ex);
        return buildResponsesEntity(
            HttpStatus.NOT_FOUND,
            BUSINESS,
            MODEL_NOT_FOUND_EXCEPTION.getValue(),
            ex.getLocalizedMessage(),
            Collections.emptyMap(),
            httpRequest.getRequestURL()
        );
    }

    @ExceptionHandler({
        MultipleEntryFoundException.class
    })
    protected ResponseEntity<Object> handleMultipleEntryFoundException(
        Exception ex,
        HttpServletRequest httpRequest
    ) {
        LOG.warn("Найдено более одного объекта:", ex);
        return buildResponsesEntity(
            HttpStatus.MULTIPLE_CHOICES,
            CRITICAL,
            MULTIPLE_FOUND_EXCEPTION.getValue(),
            ex.getLocalizedMessage(),
            Collections.emptyMap(),
            httpRequest.getRequestURL()
        );
    }

    @ExceptionHandler({
        AccountPriorityOneMoreException.class,
        AccountAlreadySignedException.class,
        OptimisticLockException.class,
        CheckValidationException.class
    })
    protected ResponseEntity<Object> handleBusinessException(
        BaseException ex,
        HttpServletRequest httpRequest
    ) {
        LOG.warn(ex.getLogMessage(), ex);
        return buildResponsesEntity(
            HttpStatus.BAD_REQUEST,
            ex.getType(),
            ex.getErrorCodeValue(),
            ex.getText(),
            ex.getErrors(),
            httpRequest.getRequestURL()
        );
    }

    @ExceptionHandler({
        EntrySaveException.class,
        OptimisticLockingFailureException.class
    })
    protected ResponseEntity<Object> handleObjectBadRequestException(
        Exception ex,
        HttpServletRequest httpRequest
    ) {
        LOG.warn("Ошибка заполнения объекта", ex);
        return buildResponsesEntity(
            HttpStatus.BAD_REQUEST,
            null,
            ENTRY_SAVE_EXCEPTION.getValue(),
            ex.getLocalizedMessage(),
            Collections.emptyMap(),
            httpRequest.getRequestURL()
        );
    }

    @ExceptionHandler(FraudDeniedException.class)
    protected ResponseEntity<Object> handleFraudDeniedException(
        FraudDeniedException ex,
        HttpServletRequest httpRequest
    ) {
        LOG.warn("Операция запрещена со стороны ФРОД-мониторинга", ex);
        return buildResponsesEntity(
            HttpStatus.BAD_REQUEST,
            BUSINESS,
            FRAUD_DENIED_EXCEPTION.getValue(),
            ex.getMessage(),
            Collections.emptyMap(),
            httpRequest.getRequestURL()
        );
    }

    @ExceptionHandler(FraudModelValidationException.class)
    protected ResponseEntity<Object> handleFraudModelValidationException(
        FraudModelValidationException ex,
        HttpServletRequest httpRequest
    ) {
        LOG.error("Ошибка валидации модели данных, отсылаемой в ППРБ Агрегатор данных ФРОД-мониторинга", ex);
        return buildResponsesEntity(
            HttpStatus.BAD_REQUEST,
            CRITICAL,
            FRAUD_MODEL_VALIDATION_EXCEPTION.getValue(),
            ex.getMessage(),
            Collections.emptyMap(),
            httpRequest.getRequestURL()
        );
    }

    @ExceptionHandler(IllegalArgumentIdsHistoryException.class)
    protected ResponseEntity<Object> handleFraudModelValidationException(
        IllegalArgumentIdsHistoryException ex,
        HttpServletRequest httpRequest
    ) {
        LOG.error("Ошибка выполнения операции с таблицей ids_history", ex);
        return buildResponsesEntity(
            HttpStatus.INTERNAL_SERVER_ERROR,
            CRITICAL,
            EXCEPTION.getValue(),
            ex.getMessage(),
            Collections.emptyMap(),
            httpRequest.getRequestURL()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleConversionFailedException(
        MethodArgumentTypeMismatchException ex,
        HttpServletRequest httpRequest
    ) {
        if (ex.getRequiredType() == FraudMetaData.class) {
            LOG.warn(ex.getCause().getMessage(), ex);
            return buildResponsesEntity(
                HttpStatus.BAD_REQUEST,
                BUSINESS,
                FRAUD_MODEL_VALIDATION_EXCEPTION.getValue(),
                MessagesTranslator.toLocale("fraud.conversion_error_from_http_header"),
                Collections.emptyMap(),
                httpRequest.getRequestURL()
            );
        } else {
            LOG.error("При выполнении операции произошла ошибка", ex);
            return buildResponsesEntity(
                HttpStatus.INTERNAL_SERVER_ERROR,
                CRITICAL,
                EXCEPTION.getValue(),
                ex.getLocalizedMessage(),
                Collections.emptyMap(),
                httpRequest.getRequestURL()
            );
        }
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleException(
        Exception ex,
        HttpServletRequest httpRequest
    ) {
        LOG.error("При выполнении операции произошла ошибка", ex);
        return buildResponsesEntity(
            HttpStatus.INTERNAL_SERVER_ERROR,
            CRITICAL,
            EXCEPTION.getValue(),
            ex.getLocalizedMessage(),
            Collections.emptyMap(),
            httpRequest.getRequestURL()
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
        return buildResponsesEntity(
            status,
            CRITICAL,
            EXCEPTION.getValue(),
            ex.getLocalizedMessage(),
            Collections.emptyMap(),
            ((ServletWebRequest) request).getRequest().getRequestURL()
        );
    }

    @NotNull
    @Override
    protected @NonNull
    ResponseEntity<Object> handleMethodArgumentNotValid(
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
            HttpStatus.BAD_REQUEST,
            BUSINESS,
            MODEL_VALIDATION_EXCEPTION.getValue(),
            MessagesTranslator.toLocale(CHECK_VALIDATION_MESSAGE),
            errors,
            ((ServletWebRequest) request).getRequest().getRequestURL()
        );
    }

    private ResponseEntity<Object> buildResponsesEntity(
        HttpStatus httpStatus,
        Error.TypeEnum type,
        int errorCode,
        String message,
        Map<String, List<String>> errors,
        StringBuffer requestUrl
    ) {
        var descriptions = errors.entrySet().stream()
            .map(value -> new Descriptions().field(value.getKey()).message(value.getValue()))
            .collect(toList());
        var errorData = new Error()
            .type(type)
            .code(errorCode)
            .message(message)
            .descriptions(descriptions);
        String url = requestUrl.toString().replaceAll("[\n\r\t]", "_");
        sentLog(type, errorData, url);
        return new ResponseEntity<>(
            errorData,
            httpStatus
        );
    }

    private void sentLog(Error.TypeEnum type, Error errorData, String url) {
        if (Objects.equals(BUSINESS, type)) {
            LOG.warn("Ошибка вызова \"{}\": {}", url, errorData);
        } else {
            LOG.error("При работе приложения возникла ошибка \"{}\": {}", url, errorData);
        }
    }
}
