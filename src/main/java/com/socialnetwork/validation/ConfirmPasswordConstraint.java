package com.socialnetwork.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ConfirmPasswordValidator.class)
public @interface ConfirmPasswordConstraint {
	String message() default "Password and confirm password don't match.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String passwordField();
    String confirmPasswordField();
}
