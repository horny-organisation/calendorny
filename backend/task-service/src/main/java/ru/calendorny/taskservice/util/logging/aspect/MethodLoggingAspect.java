package ru.calendorny.taskservice.util.logging.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@ConditionalOnProperty(prefix = "logging", value = "enabled", havingValue = "true")
public class MethodLoggingAspect {

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restControllerMethod(){}

    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void serviceMethod(){}

    @Pointcut("@within(org.springframework.stereotype.Repository)")
    public void repositoryMetod(){}

    @Pointcut("restControllerMethod()" + "||" + "repositoryMetod()" + "||" + "serviceMethod()")
    public void allBasicComponentMethods(){}

    @Before("allBasicComponentMethods()")
    public void logMethodEntry(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.debug("Begin: {}.{}() with args: {}", className, methodName, Arrays.toString(args));
    }

    @AfterReturning(pointcut = "allBasicComponentMethods()", returning = "result")
    public void logMethodExit(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.debug("End: {}.{}()with result: {}", className, methodName, result);
    }
}
