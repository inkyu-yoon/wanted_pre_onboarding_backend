package com.wanted.global.aop;

import com.wanted.global.exception.BindingException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Aspect
@Component
public class BindingCheckAop {

    @Before(value = "@annotation(com.wanted.global.annotation.BindingCheck)")
    public void before(JoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof BindingResult) {
                BindingResult br = (BindingResult) arg;
                if (br.hasErrors()) {
                    throw new BindingException(br.getFieldError().getDefaultMessage());
                }
            }
        }
    }
}
