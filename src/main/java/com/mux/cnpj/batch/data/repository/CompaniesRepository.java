package com.mux.cnpj.batch.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mux.cnpj.batch.data.entity.Company;

public interface CompaniesRepository extends JpaRepository<Company, Integer> {

}
