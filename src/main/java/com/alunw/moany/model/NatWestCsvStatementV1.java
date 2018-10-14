package com.alunw.moany.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alunw.moany.services.Utilities;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Statement parser for basic NatWest online statement format, v1.
 * 
 */
public class NatWestCsvStatementV1 implements Statement {
	
	private static final Logger LOG = LoggerFactory.getLogger(NatWestCsvStatementV1.class);
	private static final String HEADER_LINE = "Date, Type, Description, Value, Balance, Account Name, Account Number";
	private static final String LINE_REGEX = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
	
	@JsonIgnore
	private List<Account> accounts;
	@JsonIgnore
	private List<Transaction> transactions;
	private long sourceRow;
	
	@Override
	public void parseStatement(File file) throws Exception {
		
		LOG.info("parsing " + file.getName());
		
		// Clear local properties before starting parse
		accounts = new ArrayList<>();
		transactions = new ArrayList<>();
		sourceRow = 0L;
		
		String line = null;
		try (
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
		) {
			while ((line = bufferedReader.readLine()) != null) {
				sourceRow++;
				if (StringUtils.isBlank(line)) {
					// Ignore blank lines
					LOG.debug("Ignoring blank line");
				} else if (line.equals(HEADER_LINE)) {
					//Ignore header line
					LOG.debug("Ignoring header line");
				} else {
					parseLine(line, sourceRow);
				}
			}
		} catch (Exception e) {
			LOG.error("Caught exception parsing file: " + e.getMessage(), e);
			throw new Exception("Caught exception parsing file: " + e.getMessage());
		}
	}
	
	private void parseLine(String line, long row) throws Exception {
		
		Transaction transaction = null;
		
		// Split into columns
		String[] columns = line.split(LINE_REGEX);
		
		if (columns.length == 0) {
			LOG.debug("Ignoring blank line");
			return;
		} else if (columns.length != 7) {
			LOG.error("Unexpected input (line " + row + "): " + line);
			throw new Exception("Unexpected number of columns in line");
			
		} else {
			// Create transaction
			transaction = new Transaction();
			transaction.setTransactionDate(Utilities.parseStatementDate(columns[0]));
			transaction.setType(columns[1]);
			transaction.setDescription(StringUtils.strip(columns[2], "\"'"));
			transaction.setSourceRow(sourceRow);
			
			// Get account details - create new object for each account during parsing (these can be checked during save).
			String accNum = StringUtils.strip(columns[6], "\"'");
			String accName = StringUtils.strip(columns[5], "\"'");
			
			// Get local account object, if exists - else create
			Account account = getStatementAccountByAccNum(accNum);
			if (account == null) {
				account = new Account();
				account.setName(accName);
				account.setAccNum(accNum);
				accounts.add(account);
				LOG.debug("Created new account: " + account);
			}
			
			// Only process non-blank amounts
			String value = columns[3];
			if (StringUtils.isNotBlank(value)) {
				BigDecimal statementAmount = new BigDecimal(value);
				transaction.setStatementAmount(statementAmount);
				transaction.setNetAmount(statementAmount.multiply(account.getType().getMultiplier()));
			} else {
				transaction.setNetAmount(BigDecimal.ZERO);
			}
			
			// Only process non-blank statement balances
			value = columns[4];
			if (StringUtils.isNotBlank(value)) {
				transaction.setStmtBalance(new BigDecimal(value));
			}
			
			transaction.setAccount(account);
		}
		
		transactions.add(transaction);
		LOG.debug("Created new transaction: " + transaction);
	}
	
	/**
	 * Return first matching account in local set by account number.
	 * 
	 * @param accNum
	 * @return Account
	 */
	private Account getStatementAccountByAccNum(String accNum) {
		for (Account account : accounts) {
			if (account.getAccNum().equals(accNum)) {
				// Match
				return account;
			}
		}
		return null;
	}
	
	@Override
	public List<Transaction> getTransactions() {
		return transactions;
	}

	@Override
	public List<Account> getAccounts() {
		return accounts;
	}

	@Override
	public String getDisplayName() {
		return "NatWest CSV Statement Importer (v1)";
	}
}
