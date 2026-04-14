package com.alunw.moany.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.alunw.moany.model.Category;

public interface CategoryRepository extends CrudRepository<Category, String> {

	List<Category> findAll();

}
