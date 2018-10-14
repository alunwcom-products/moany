package com.alunw.moany.repository;

import org.springframework.data.repository.CrudRepository;

import com.alunw.moany.model.User;

public interface UserRepository extends CrudRepository<User, String> {

	User findByUsername(String username);

}
