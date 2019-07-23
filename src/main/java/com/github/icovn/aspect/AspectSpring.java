package com.github.icovn.aspect;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.naming.AuthenticationException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
@Component
public class AspectSpring {
    private boolean isChecked = false;
    private static Logger logger = LoggerFactory.getLogger(AspectSpring.class);

    @Before("execution(* com.github.icovn..aop..*(..))")
    public void logIn() throws IOException, AuthenticationException {
        int countLogin = 0;
        if (!isChecked) {
            BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
            logger.info("Username: ");
            String name = bf.readLine();
            logger.info("Password: ");
            String pwd = bf.readLine();
            while (!(name.equals("admin") && pwd.equals("1111"))) {
                countLogin++;
                if (countLogin > 2)
                    throw new AuthenticationException("Too many login errors !");
                logger.info("Wrong authentication !");
                logger.info("Username: ");
                name = bf.readLine();
                logger.info("Password: ");
                pwd = bf.readLine();
            }
            isChecked = true;
        }
    }

    @Around("execution(* com.github.icovn..aop..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        logger.info("{} executed in {} ms", joinPoint.getSignature(), executionTime);

        Object[] signatureArgs = joinPoint.getArgs();
        CodeSignature signatures = (CodeSignature) joinPoint.getSignature();
        for (int i = 0; i < signatureArgs.length; i++) {
            logger.info("Parameter: {}\t{}\t{}\t{}", i + 1, signatures.getParameterTypes()[i],
                    signatures.getParameterNames()[i], signatureArgs[i]);
        }

        logger.info("Output datatype: {} ", proceed.getClass().getName());
        logger.info("Value: {} ", proceed);
        return proceed;
    }
}

