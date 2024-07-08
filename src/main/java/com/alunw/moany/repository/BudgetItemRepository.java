package com.alunw.moany.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.alunw.moany.model.BudgetItem;

public interface BudgetItemRepository extends CrudRepository<BudgetItem, String> {
	List<BudgetItem> findAll();
}
