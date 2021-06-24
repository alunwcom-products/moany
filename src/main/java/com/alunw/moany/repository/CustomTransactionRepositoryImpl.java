package com.alunw.moany.repository;

import com.alunw.moany.model.Account;
import com.alunw.moany.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class CustomTransactionRepositoryImpl implements CustomTransactionRepository {

    private static final Logger logger = LoggerFactory.getLogger(CustomTransactionRepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByAccount(List<Account> accounts, LocalDate startDate, LocalDate endDate) {

        logger.debug("getTransactionsByAccount(" + accounts + ", " + startDate + ", " + endDate + ")");

        // Don't run query if parameters are missing
        if (startDate == null || endDate == null || accounts == null || accounts.isEmpty()) {
            return Collections.emptyList();
        }

        TypedQuery<Transaction> typedQuery = em.createQuery("from Transaction where account in :acc "
                + "and transactionDate >= :startDate "
                + "and transactionDate <= :endDate "
                + "order by transactionDate, sourceRow asc", Transaction.class);
        typedQuery.setParameter("acc", accounts);
        typedQuery.setParameter("startDate", startDate);
        typedQuery.setParameter("endDate", endDate);

        List<Transaction> results = typedQuery.getResultList();

        logger.debug("Retrieved {} transactions(s)", results.size());

        return results;
    }
}
