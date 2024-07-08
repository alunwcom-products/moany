//package com.alunw.moany.services;
//
//import java.util.List;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.persistence.TypedQuery;
//import javax.transaction.Transactional;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.alunw.moany.model.Account;
//import com.alunw.moany.model.Statement;
//import com.alunw.moany.model.Transaction;
//import com.alunw.moany.model.TransactionType;
//import com.alunw.moany.repository.AccountRepository;
//import com.alunw.moany.repository.TransactionRepository;
//
//@Component
//public class StatementService {
//
//	private static final Logger logger = LoggerFactory.getLogger(StatementService.class);
//
//	@Autowired
//	private AccountRepository accountRepo;
//
//	@Autowired
//	private TransactionRepository transactionRepo;
//
//	@PersistenceContext
//	private EntityManager em;
//
//
//	/**
//	 * Saves (persists) statement transactions, and optionally accounts, returning the number of saved/persisted
//	 * transactions.
//	 *
//	 * @param statement Statement object to be saved/persisted.
//	 * @param sourceName Statement source file name, used as a label against transactions.
//	 * @param autoAccountCreation Create undefined accounts automatically?
//	 * @param failOnDuplicateTransaction Abort (and rollback) saving of statement if a duplicate transaction is found?
//	 *   (If false, then duplicate transactions will be ignored, but other transactions, and accounts, will be saved.)
//	 * @return int The number of transactions saved to the database.
//	 * @throws Exception Any captured exception in the upload (persist to database).
//	 */
//	@Transactional // TODO check
//	public int save(Statement statement, String sourceName, boolean autoAccountCreation, boolean failOnDuplicateTransaction) throws Exception {
//
//		int transactionCount = 0;
//
//		try {
//			// Save accounts if 'auto create', else fail if they don't exist.
//			for (Account acc : statement.getAccounts()) {
//				Account storedAcc = accountRepo.findFirstByAccNum(acc.getAccNum());
//				if (storedAcc == null) {
//					// account not found
//					if (autoAccountCreation) {
//						// create account
//						accountRepo.save(acc);
//						logger.debug("Saved account record: " + acc.getId());
//					} else {
//						// throw exception
//						throw new Exception("Account (" + acc.getAccNum() + ") doesn't exist, and auto-create is false. Aborting save!");
//					}
//				} else {
//					// update transactions using this account (so transaction account is persisted version)
//					updateAccount(acc, storedAcc, statement.getTransactions());
//					logger.debug("Using existing account record: " + storedAcc.getId());
//				}
//			}
//
//			// Save transactions - checking for duplicates.
//			for (Transaction tran : statement.getTransactions()) {
//				// Check if transaction exists on database.
//				if (getMatchingTransaction(tran).isEmpty()) {
//					// No duplicate found - save transaction
//					tran.setSourceType(TransactionType.STATEMENT);
//					tran.setSourceName(sourceName);
//					// Update net amount (using actual account record)
//					if (tran.getStatementAmount() != null) {
//						tran.setNetAmount(tran.getStatementAmount().multiply(tran.getAccount().getType().getMultiplier()));
//					}
//
//					transactionRepo.save(tran);
//					transactionCount++;
//					logger.debug("Saved transaction record: " + tran.getId());
//				} else {
//					// Duplicate found - abort or ignore?
//					if (failOnDuplicateTransaction) {
//						// Fail upload
//						logger.warn("An existing transaction has been found that matches a statement transaction. Aborting save. " + tran);
//						throw new Exception("Existing transactions have been found that match that just parsed. Aborting import. " + tran);
//					} else {
//						// Just warn and ignore transaction
//						logger.warn("An existing transaction has been found that matches a statement transaction. " + tran);
//					}
//					for (Transaction t : getMatchingTransaction(tran)) {
//						logger.info("Existing transaction: " + t);
//					}
//				}
//			}
//
//		} catch (Exception e) {
//			logger.error("Unable to save statement, rolling back... " + e.getMessage());
//			transactionCount = 0;
//			// Re-throw exception
//			throw e;
//		}
//
//		return transactionCount;
//	}
//
//	private void updateAccount(Account parsedAccount, Account persistedAccount, List<Transaction> transactions) {
//		for (Transaction t : transactions) {
//			if (t.getAccount().getAccNum().equals(persistedAccount.getAccNum())) {
//				t.setAccount(persistedAccount);
//			}
//		}
//	}
//
//	private List<Transaction> getMatchingTransaction(Transaction transaction) {
//
//		TypedQuery<Transaction> typedQuery = em.createQuery("from Transaction where transactionDate = :transactionDate "
//				+ "and type = :type and description = :description and statementAmount = :statementAmount "
//				+ "and stmtBalance = :stmtBalance and account = :account order by entryDate desc", Transaction.class);
//		typedQuery.setParameter("transactionDate", transaction.getTransactionDate());
//		typedQuery.setParameter("type", transaction.getType());
//		typedQuery.setParameter("description", transaction.getDescription());
//		typedQuery.setParameter("statementAmount", transaction.getStatementAmount());
//		typedQuery.setParameter("stmtBalance", transaction.getStmtBalance());
//		typedQuery.setParameter("account", transaction.getAccount());
//
//		List<Transaction> results = typedQuery.getResultList();
//		logger.debug(String.format("Retrieved %d transaction(s)", results.size()));
//
//		return results;
//	}
//
//}
