package com.alunw.moany.services;

import com.alunw.moany.model.Account;
import com.alunw.moany.model.AccountType;
import com.alunw.moany.model.Transaction;
import com.alunw.moany.repository.AccountRepository;
import com.alunw.moany.repository.TransactionRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

//import javax.annotation.PostConstruct;
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class TransactionService {

	private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

	@Autowired
	private TransactionRepository transactionRepo;

	@Autowired
	private AccountRepository accountRepo;

	@PersistenceContext
	private EntityManager em;

	@PostConstruct
	public void init() {}

	/**
	 * TODO
	 *
	 * Helper method to (re-)calculate a transaction balances for an account starting from a single transaction *with*
	 * a statement balance defined. This also updates the account record with the starting date/balance.
	 *
	 * @param transactionId Transaction ID
	 */
	@Transactional
	public void rebalanceAccountFromTransaction(String transactionId) {

		logger.debug("rebalanceAccountFromTransaction({})", transactionId);

		Optional<Transaction> tran = transactionRepo.findById(transactionId);

		if (!tran.isPresent()) {
			logger.warn("No transaction found with supplied ID [{}] - skipping rebalance!", transactionId);
			return;
		} else {
			logger.debug("transaction = [{}]", tran.get());
		}

		if (tran.get().getStmtBalance() == null) {
			logger.warn("Transaction [{}] has no statement balance set, cannot re-balance from here - bad request!", transactionId);
			return;
		}

		Account account = tran.get().getAccount();
		logger.info("rebalancing account = {}", account);

		List<Account> accounts = new ArrayList<>();
		accounts.add(account);

		// Re-balance account from transaction forwards
		List<Transaction> transactionsForwards = transactionRepo.findTransactionsByAccount(accounts, tran.get().getTransactionDate(), Utilities.getLatestDate());
		BigDecimal balance = tran.get().getStmtBalance().multiply(account.getType().getMultiplier());
		boolean balancingStarted = false;
		for (Transaction t : transactionsForwards) {

			// Ensure net amount is set and up-to-date
			updateNetAmount(t, account.getType());

			// update transactions after the starting transaction (add amount to balance)
			if (balancingStarted) {

				balance = balance.add(t.getNetAmount());
				t.setAccBalance(balance);
				transactionRepo.save(t);

				logger.debug("[id = {}, date = {}, row = {}, net amount= {}, balance = {}]", t.getId(), t.getTransactionDate(), t.getSourceRow(), t.getNetAmount(), t.getAccBalance());
			}

			// ignore any initial transactions before the start transaction, and set net amount and balance for start transaction
			if (!balancingStarted && tran.get().getId().equals(t.getId())) {
				balancingStarted = true;

				t.setAccBalance(balance);
				transactionRepo.save(t);

				logger.debug("[id = {}, date = {}, row = {}, net amount= {}, balance = {}]", t.getId(), t.getTransactionDate(), t.getSourceRow(), t.getNetAmount(), t.getAccBalance());
			}
		}

		// Re-balance account from transaction backwards
		List<Transaction> transactionsBackwards = transactionRepo.findTransactionsByAccount(accounts, Utilities.getEarliestDate(), tran.get().getTransactionDate());
		balance = tran.get().getStmtBalance().multiply(account.getType().getMultiplier());
		balancingStarted = false;
		BigDecimal previousNetAmount = null;
		for (int i = transactionsBackwards.size(); i > 0; i--) {

			Transaction t = transactionsBackwards.get(i - 1);

			// Ensure net amount is set and up-to-date
			updateNetAmount(t, account.getType());

			// update transactions before the starting transaction (deduct amount to balance)
			if (balancingStarted) {

				balance = balance.subtract(previousNetAmount);

				previousNetAmount = t.getNetAmount();
				t.setAccBalance(balance);
				transactionRepo.save(t);

				logger.debug("[id = {}, date = {}, row = {}, net amount= {}, balance = {}]", t.getId(), t.getTransactionDate(), t.getSourceRow(), t.getNetAmount(), t.getAccBalance());
			}

			// ignore any initial transactions before the start transaction, and set net amount and balance for start transaction
			if (!balancingStarted && tran.get().getId().equals(t.getId())) {
				balancingStarted = true;

				previousNetAmount = t.getNetAmount();
				t.setAccBalance(balance);
				transactionRepo.save(t);

				logger.debug("[id = {}, date = {}, row = {}, net amount= {}, balance = {}]", t.getId(), t.getTransactionDate(), t.getSourceRow(), t.getNetAmount(), t.getAccBalance());
			}
		}

		// Update account record: start date, starting balance
		if (!transactionsBackwards.isEmpty()) {
			Transaction t = transactionsBackwards.get(0);
			account.setStartBalance(t.getAccBalance().subtract(t.getNetAmount()));
			accountRepo.save(account);
		}
	}

	/**
	 * Update running balances
	 *
	 * Work through all accounts - starting from start date, and also update net_amount
	 *
	 * @param acc (Optional) account to rebalance from starting balance. If null, then all accounts are rebalanced.
	 */
	@Transactional // TODO check workings of this
	public void recalculateBalances(Account acc) {

		logger.debug("recalculateBalances({})", acc);

		List<Account> accounts = Collections.emptyList();
		if (acc != null) {
			accounts = new ArrayList<>(1);
			accounts.add(acc);
		} else {
			// get all accounts
			accounts = accountRepo.findAll();
		}

		// for each account
		for (Account account : accounts) {

			// start running balance
			BigDecimal runningBalance = account.getStartBalance();

			// get transactions from account start date

			List<Account> accountList = new ArrayList<>();
			accountList.add(account);
			List<Transaction> transactions = transactionRepo.findTransactionsByAccount(accountList, Utilities.getEarliestDate(), Utilities.getLatestDate());

			for (Transaction transaction : transactions) {

				// Ensure net amount is set and up-to-date
				updateNetAmount(transaction, account.getType());

				runningBalance = runningBalance.add(transaction.getNetAmount());
				transaction.setAccBalance(runningBalance);
				logger.debug("Updating net amount [" + transaction + "] = " + runningBalance);

				logger.debug("post-recalc: statement_amount = {}, net_amount = {}, account_balance = {}",
						transaction.getStatementAmount(), transaction.getNetAmount(), transaction.getAccBalance());

				transactionRepo.save(transaction);
			}
		}
	}

	/**
	 * Helper method to ensure the net amount is set and up-to-date.
	 *
	 * @param t
	 * @param type
	 */
	private void updateNetAmount(Transaction t, AccountType type) {
		if (t.getStatementAmount() != null) {
			// if there is a statement amount - recalculate net amount (in case account type has changed)
			t.setNetAmount(t.getStatementAmount().multiply(type.getMultiplier()));
		} else if (t.getNetAmount() == null) {
			// ensure the net amount is not null
			t.setNetAmount(new BigDecimal(0));
		}
	}
}
