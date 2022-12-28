package ru.sberbank.pprb.sbbol.partners.aspect.validator;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.exception.CheckValidationException;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Aspect
public class FraudValidatorAspect {

    private final Validator validator;

    public FraudValidatorAspect(Validator validator) {
        this.validator = validator;
    }

    @Pointcut("execution(* ru.sberbank.pprb.sbbol.partners..*Controller.*(..))")
    void callPartnersController() {
    }

    @Before(value = "callPartnersController() && @annotation(fraudValid)", argNames = "joinPoint, fraudValid")
    void validation(JoinPoint joinPoint, FraudValid fraudValid) {
        var args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof FraudMetaData) {
                var fraudMetaData = (FraudMetaData) arg;
                Set<ConstraintViolation<FraudMetaData>> errors = validator.validate(fraudMetaData);
                if (!CollectionUtils.isEmpty(errors)) {
                    Map<String, List<String>> validationErrors = new HashMap<>();
                    for (ConstraintViolation<FraudMetaData> error : errors) {
                        var key = String.format("fraudMetaData.%s", error.getPropertyPath().toString());
                        var value = error.getMessage();
                        var values = validationErrors.get(key);
                        if (values == null) {
                            validationErrors.put(key, new ArrayList<>(Arrays.asList(value)));
                        } else {
                            values.add(value);
                        }
                    }
                    throw new CheckValidationException(validationErrors);
                }
            }
        }
    }
}
