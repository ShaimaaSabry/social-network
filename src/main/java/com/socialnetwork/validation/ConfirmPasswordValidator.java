package com.socialnetwork.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;

import com.socialnetwork.dto.CreateUserRequest;

public class ConfirmPasswordValidator implements ConstraintValidator<ConfirmPasswordConstraint, CreateUserRequest> {
	private String passwordField;
	private String confirmPasswordField;
	private String message;

	@Override
	public void initialize(ConfirmPasswordConstraint constraint) {
		passwordField = constraint.passwordField();
		confirmPasswordField = constraint.confirmPasswordField();
		message = constraint.message();
	}

	@Override
	public boolean isValid(CreateUserRequest createUserRequest, ConstraintValidatorContext cxt) {
		String password = (String) new BeanWrapperImpl(createUserRequest).getPropertyValue(passwordField);
		String confirmPassword = (String) new BeanWrapperImpl(createUserRequest).getPropertyValue(confirmPasswordField);
		if (password != null && confirmPassword != null && confirmPassword.equals(password)) {	
		return true;
		} else {
			cxt.buildConstraintViolationWithTemplate(message);
			return false;
		}
	}
}
