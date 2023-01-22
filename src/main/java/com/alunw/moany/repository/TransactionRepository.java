package com.alunw.moany.repository;

import com.alunw.moany.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

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
			"  trans_date,\n" +
			"  sum(credit) as incoming,\n" +
			"  sum(debit) as outgoing,\n" +
			"  sum(credit) + sum(debit) as net,\n" +
			"  sum(sum(credit) + sum(debit)) over (order by trans_date) as balance\n" +
			"from data\n" +
			"group by trans_date", nativeQuery = true)
	List<Object> findByQuery();
}
