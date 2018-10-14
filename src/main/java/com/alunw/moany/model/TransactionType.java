package com.alunw.moany.model;

/**
 * Defines the various transaction types, and whether they should be editable or not. Note that all transactions
 * should allow the editing of categories and comments.
 *  
 * @author aluwilliam
 *
 */
public enum TransactionType {
	STATEMENT(false),
	BUDGETING(true),
	MANUAL(true);
	
	private final boolean editable;
	
	private TransactionType(boolean editable) {
		this.editable = editable;
	}
	
	public boolean isEditable() {
		return editable;
	}
}
