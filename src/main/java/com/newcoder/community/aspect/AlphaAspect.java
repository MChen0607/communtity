package com.newcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.io.OutputStream;

@Component
@Aspect
public class AlphaAspect {

//    @Pointcut("execution(* com.newcoder.community.service.*.*(..))")
//    public void pointcut() {
//
//    }

//    @Before("pointcut()")
//    public void before() {
//        System.out.println("before");
//    }
//
//    @After("pointcut()")
//    public void after() {
//        System.out.println("after");
//    }
//
//    @AfterReturning("pointcut()")
//    public void afterReturning() {
//        System.out.println("afterReturning");
//    }
//
//    @AfterThrowing("pointcut()")
//    public void afterThrowing() {
//        System.out.println("afterThrowing");
//    }
//
//    @Around("pointcut()")
//    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
//        System.out.println("aroundBefore");
//        Object obj = joinPoint.proceed();
//        System.out.println("aroundAfter");
//        return obj;
//    }
}
