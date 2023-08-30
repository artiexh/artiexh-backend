package com.artiexh.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BusinessCodeValidator implements ConstraintValidator<BusinessCode, String> {
	@Override
	public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
		if (s.length() == 10 || s.length() == 13) {
			return true;
		}
		return false;
	}
}
