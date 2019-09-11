package com.wearewaes.aspects;

import com.wearewaes.exceptions.DocumentNotFoundException;
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
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // Log info (method name, args, returnValue / returnObject) on every call of every method of CompareService
    @AfterReturning(pointcut = " execution(* com.wearewaes.service.CompareService+.*(..))",returning="returnValue")
    public void CompareService(JoinPoint joinPoint, Object returnValue) {
        log.info("Enter: {}.{}() with argument[s] = {} Return Object => {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()), returnValue);
    }

    // Log info (method name, args, returnValue / returnObject, exception message) on every call of every method of CompareService that throws an exception
    @AfterThrowing(pointcut = " execution(* com.wearewaes.service.CompareService+.*(..))",
            throwing="exception")
    public void logAfterThrowing(JoinPoint joinPoint, DocumentNotFoundException exception){
        log.info("Exception thrown in: {}.{}() with argument[s] = {} message => {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()), exception.getMessage());
    }
}
