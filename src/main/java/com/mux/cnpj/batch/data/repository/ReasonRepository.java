package com.mux.cnpj.batch.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mux.cnpj.batch.data.entity.Reason;

public interface ReasonRepository extends JpaRepository<Reason, Integer> {

	@Query("select coalesce(max(r.id), 0) from Reason r")
	Long getMaxId();

}
