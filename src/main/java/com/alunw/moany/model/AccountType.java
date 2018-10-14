package com.alunw.moany.model;

import java.math.BigDecimal;

public enum AccountType {
	DEBIT(new BigDecimal(1)), 
	CREDIT(new BigDecimal(-1));
	
	private final BigDecimal multiplier;
	
	private AccountType(BigDecimal multiplier) {
		this.multiplier = multiplier;
	}
	
	public BigDecimal getMultiplier() {
		return multiplier;
	}
}
