package com.alunw.moany.repository;

import com.alunw.moany.model.DailyTotal;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DailyTotalRepository extends CrudRepository<DailyTotal, String> {
    List<DailyTotal> findAll();
}
