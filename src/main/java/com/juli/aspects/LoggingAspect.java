package com.juli.aspects;

import com.juli.exceptions.DocumentNotFoundException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
/**
 * Logging class responsible for logging on every method call of CompareService and every exception thrown
 */
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     *  Log info (method name, args, returnValue / returnObject) on every call of every method of CompareService
     * @param joinPoint which method calls should traced
     * @param returnValue the object returned by the method invocation
     */
    @AfterReturning(pointcut = " execution(* com.juli.service.JSONCompareService+.*(..))",returning="returnValue")
    public void CompareService(JoinPoint joinPoint, Object returnValue) {
        log.info("Enter: {}.{}() with argument[s] = {} Return Object => {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()), returnValue);
    }

    /**
     *     Log info (method name, args, returnValue / returnObject, exception message) on every call of every method of CompareService that throws an exception
     * @param joinPoint log on every exception thrown in CompareService
     * @param exception exception thrown
     */
    @AfterThrowing(pointcut = " execution(* com.juli.service.JSONCompareService+.*(..))",
            throwing="exception")
    public void logAfterThrowing(JoinPoint joinPoint, Exception exception){
        log.info("Exception thrown in: {}.{}() with argument[s] = {} message => {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()), exception.getMessage());
    }
}
