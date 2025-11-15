package com.cheack.softwareengineering.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * 정책: 8~20자, 영문/숫자/특수(~!@#$%^&*+) 각 1+개 포함
 */
public class PasswordValidator implements ConstraintValidator<Password, String> {

    private static final Pattern LENGTH = Pattern.compile("^.{8,20}$");
    private static final Pattern HAS_ALPHA = Pattern.compile(".*[A-Za-z].*");
    private static final Pattern HAS_DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern HAS_SPECIAL = Pattern.compile(".*[~!@#$%^&*+].*");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return LENGTH.matcher(value).matches()
                && HAS_ALPHA.matcher(value).matches()
                && HAS_DIGIT.matcher(value).matches()
                && HAS_SPECIAL.matcher(value).matches();
    }
}