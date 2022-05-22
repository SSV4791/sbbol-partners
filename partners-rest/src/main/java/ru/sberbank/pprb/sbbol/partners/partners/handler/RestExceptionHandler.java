package ru.sberbank.pprb.sbbol.partners.partners.handler;

import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.orm.hibernate5.HibernateOptimisticLockingFailureException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.sberbank.pprb.sbbol.partners.exception.BadRequestException;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.EntrySaveException;
import ru.sberbank.pprb.sbbol.partners.exception.MissingValueException;
import ru.sberbank.pprb.sbbol.partners.exception.ModelValidationException;
import ru.sberbank.pprb.sbbol.partners.exception.PartnerMigrationException;
import ru.sberbank.pprb.sbbol.partners.model.Error;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionHandler.class);
    private static final String FILL_OBJECT_MESSAGE_EXCEPTION = "Ошибка заполнения объекта";

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<?> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest httpRequest
        ) {
        LOG.error("Нарушение ограничений уникальности в БД", ex);
        return buildResponseEntity(
            HttpStatus.BAD_REQUEST,
            ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(". ")),
            httpRequest.getRequestURL()
        );
    }

    @ExceptionHandler(
        {
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

    @ExceptionHandler(ModelValidationException.class)
    protected ResponseEntity<Object> handleObjectModelValidationException(
            ModelValidationException ex,
            HttpServletRequest httpRequest
        ) {
        LOG.error(FILL_OBJECT_MESSAGE_EXCEPTION, ex);
        return buildResponseEntity(
            HttpStatus.BAD_REQUEST,
            ex.getErrors(),
            httpRequest.getRequestURL()
        );
    }

    @ExceptionHandler(MissingValueException.class)
    protected ResponseEntity<Object> handleMissingValueException(
            MissingValueException ex,
            HttpServletRequest httpRequest
        ) {
        LOG.error(FILL_OBJECT_MESSAGE_EXCEPTION, ex);
        return buildResponseEntity(
            HttpStatus.BAD_REQUEST,
            ex.getErrors(),
            httpRequest.getRequestURL()
        );
    }

    @ExceptionHandler(
        {
            BadRequestException.class,
            OptimisticLockingFailureException.class,
            EntrySaveException.class
        })
    protected ResponseEntity<Object> handleObjectBadRequestException
        (
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
    protected @NonNull
    ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatus status,
            @NonNull WebRequest request
        ) {
        return buildResponseEntity(status,
            ex.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(". ")),
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
        return buildResponseEntity(
            status,
            Collections.singletonList(errorDesc),
            requestUrl
        );
    }

    private ResponseEntity<Object> buildResponseEntity(
            HttpStatus status,
            List<String> errorsDesc,
            StringBuffer requestUrl
        ) {
        var errorData = new Error().code(status.name()).text(errorsDesc);
        String url = requestUrl.toString().replaceAll("[\n\r\t]", "_");
        LOG.error("Ошибка вызова \"{}\": {}", url, errorData);
        return new ResponseEntity<>(
            errorData,
            status
        );
    }
}
