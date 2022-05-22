package ru.sberbank.pprb.sbbol.partners.aspect.validation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.exception.ModelValidationException;

import java.lang.annotation.Annotation;
import java.util.ArrayList;

/**
 * Аспект, обрабатывающий вызов методов с валидацией, отмеченных аннотацией {@link Validation}
 */
@Aspect
@Order(10)
public class ValidationAspect {

    private final ApplicationContext applicationContext;

    public ValidationAspect(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Метод для валидации объектов в РЕСТ клиентах.
     */
    @SuppressWarnings("unchecked")
    @Before("execution(* ru.sberbank.pprb.sbbol.partners..*Service.*(..))")
    public void validate(JoinPoint call) {
        var signature = (MethodSignature) call.getSignature();
        var method = signature.getMethod();
        var parameterAnnotations = method.getParameterAnnotations();
        var args = call.getArgs();
        for (int argIndex = 0; argIndex < parameterAnnotations.length; argIndex++) {
            for (Annotation annotation : parameterAnnotations[argIndex]) {
                if (!(annotation instanceof Validation)) {
                    continue;
                }
                var type = ((Validation) annotation).type();
                Validator bean = getBean(type, type.getName());
                var errors = new ArrayList<String>();
                bean.validator(errors, args[argIndex]);
                if (CollectionUtils.isEmpty(errors)) {
                    return;
                }
                throw new ModelValidationException(errors);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getBean(Class<? extends T> clazz, String beanName) {
        if (!applicationContext.containsBean(beanName)) {
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(clazz)
                .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
            beanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
        }
        return (T) applicationContext.getBean(beanName);
    }
}
