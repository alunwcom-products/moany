package com.alunw.moany.repository;

import com.alunw.moany.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String>, CustomTransactionRepository {
	
	List<Transaction> findAll();
	List<Transaction> findBySourceName(String sourceName);
}
