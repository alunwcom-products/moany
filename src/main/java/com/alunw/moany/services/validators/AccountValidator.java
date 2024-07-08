//package com.alunw.moany.services.validators;
//
//import org.springframework.stereotype.Component;
//import org.springframework.validation.Errors;
//import org.springframework.validation.ValidationUtils;
//import org.springframework.validation.Validator;
//
//import com.alunw.moany.model.Account;
//
//@Component
//public class AccountValidator implements Validator {
//
//	@Override
//	public boolean supports(Class<?> clazz) {
//		return Account.class.equals(clazz);
//	}
//
//	@Override
//	public void validate(Object target, Errors errors) {
//		// TODO check start balance precision
//		ValidationUtils.rejectIfEmpty(errors, "name", "name.empty", "Account name is mandatory");
//		ValidationUtils.rejectIfEmpty(errors, "accNum", "accNum.empty", "Account number is mandatory");
//		ValidationUtils.rejectIfEmpty(errors, "type", "type.empty", "Account type is mandatory");
//		ValidationUtils.rejectIfEmpty(errors, "startBalance", "startBalance.empty", "Start balance is mandatory");
//	}
//}
