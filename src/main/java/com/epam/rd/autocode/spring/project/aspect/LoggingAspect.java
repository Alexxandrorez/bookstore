package com.epam.rd.autocode.spring.project.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.epam.rd.autocode.spring.project.service.*.*(..))")
    public void serviceMethods() {}

    @Before("serviceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        log.info(">>> [AOP] Calling method: {} with arguments: {}",
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        log.info("<<< [AOP] Method {} completed successfully. Result: {}",
                joinPoint.getSignature().getName(), result);
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void logError(JoinPoint joinPoint, Exception ex) {
        log.error("!!! [AOP] Error in method {}: {}",
                joinPoint.getSignature().getName(), ex.getMessage());
    }
}