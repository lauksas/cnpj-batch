package com.mux.cnpj.batch.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mux.cnpj.batch.data.entity.Reason;

public interface ReasonRepository extends JpaRepository<Reason, Integer> {

}
