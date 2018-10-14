package com.alunw.moany.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.alunw.moany.model.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction, String> {
	
	List<Transaction> findAll();
	List<Transaction> findBySourceName(String sourceName);
}
