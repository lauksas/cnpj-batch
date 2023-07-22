package com.mux.cnpj.repository;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mux.cnpj.batch.data.entity.Company;
import com.mux.cnpj.batch.data.repository.CompaniesRepository;

@SpringBootTest
public class CompaniesRepositoryTest {

	@Autowired
	private CompaniesRepository companiesRepository;

	@Test
	public void insertCompany() {
		Company company = Company.builder()
				.cnpj(41281710)
				.name("MURILO ELIAS DE SOUZA NETO 09804828790")
				.legalNature(2135)
				.closeDownReason(50)
				.socialCapital(new BigDecimal("1000,00"))
				.legalNature(01)
				.companySize(0)
				.build();
		companiesRepository.save(company);
	}
}
