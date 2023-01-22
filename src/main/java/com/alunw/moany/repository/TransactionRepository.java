package com.alunw.moany.repository;

import com.alunw.moany.model.DailyTotals;
import com.alunw.moany.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

public interface TransactionRepository extends JpaRepository<Transaction, String>, CustomTransactionRepository {

	List<Transaction> findAll();
	List<Transaction> findBySourceName(String sourceName);

	@Query(value = "with data as (\n" +
			"  select\n" +
			"    trans_date,\n" +
			"    if(net_amount > 0, net_amount, 0) as credit,\n" +
			"    if(net_amount <= 0, net_amount, 0) as debit\n" +
			"  from transactions\n" +
			"  order by trans_date\n" +
			")\n" +
			"select\n" +
			"  trans_date as transactionDate,\n" +
			"  sum(credit) as incomingAmount,\n" +
			"  sum(debit) as outgoingAmount,\n" +
			"  sum(credit) + sum(debit) as netAmount,\n" +
			"  sum(sum(credit) + sum(debit)) over (order by trans_date) as runningBalance\n" +
			"from data\n" +
			"group by trans_date", nativeQuery = true)
	List<Map<String, Object>> findByQuery();
}
