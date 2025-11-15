package com.cheack.softwareengineering.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "비밀번호 형식이 올바르지 않습니다(8~20자, 영문/숫자/특수문자(~!@#$%^&*+) 각 1자 이상)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}