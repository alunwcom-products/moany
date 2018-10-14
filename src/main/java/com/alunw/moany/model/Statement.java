package com.alunw.moany.model;

import java.io.File;
import java.util.List;

public interface Statement {
	void parseStatement(File file) throws Exception;
	List<Transaction> getTransactions();
	List<Account> getAccounts();
	String getDisplayName();
}
