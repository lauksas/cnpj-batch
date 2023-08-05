package com.mux.cnpj.batch.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mux.cnpj.batch.data.entity.PersonType;

public interface PersonTypeRepository extends JpaRepository<PersonType, Integer> {

}
