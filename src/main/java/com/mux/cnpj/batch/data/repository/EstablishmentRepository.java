package com.mux.cnpj.batch.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import com.mux.cnpj.batch.data.entity.CnpjId;
import com.mux.cnpj.batch.data.entity.Establishment;

import jakarta.persistence.QueryHint;

public interface EstablishmentRepository extends JpaRepository<Establishment, CnpjId> {

	@Query("select e.cnpjId.cnpj from Establishment e where e.cnpjId.cnpj = :cnpj")
	@QueryHints({@QueryHint(name = "org.hibernate.fetchSize", value = "1")})
	Integer findByCnpj(Integer cnpj);

}
