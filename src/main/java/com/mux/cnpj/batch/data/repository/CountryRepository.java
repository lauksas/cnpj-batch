package com.mux.cnpj.batch.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mux.cnpj.batch.data.entity.Country;

public interface CountryRepository extends JpaRepository<Country, Integer> {

	@Query("select coalesce(max(c.id), 0) from Country c")
	Long getMaxId();

}
