package com.mux.cnpj.batch.job;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.JdbcTransactionManager;

import com.mux.cnpj.batch.job.step.CnaeImportStepBuilder;
import com.mux.cnpj.batch.job.step.CompaniesImportStepBuilder;
import com.mux.cnpj.batch.job.step.EstabilishmentsImportStepBuilder;
import com.mux.cnpj.batch.job.step.LegalNatureImportStepBuilder;
import com.mux.cnpj.batch.job.step.MunicipalityImportStepBuilder;
import com.mux.cnpj.batch.job.step.ReasonsImportStepBuilder;

@Configuration
@EnableBatchProcessing(dataSourceRef = "batchDataSource", transactionManagerRef = "batchTransactionManager")
public class CnpjImportJobConfig {

	@Autowired
	@Qualifier("batchDataSource")
	DataSource batchDataSource;

	@Bean
	public JdbcTransactionManager batchTransactionManager() {
		return new JdbcTransactionManager(batchDataSource);
	}

	@Bean
	public Job importCnpjJob(
			JobRepository jobRepository,
			ReasonsImportStepBuilder reasonStepBuilder,
			MunicipalityImportStepBuilder municipalityImportStepBuilder,
			LegalNatureImportStepBuilder legalNatureImportStepBuilder,
			CnaeImportStepBuilder cnaeStepBuilder,
			EstabilishmentsImportStepBuilder estabilishmentStepBuilder,
			CompaniesImportStepBuilder companyStepBuilder) {

		Job job = new JobBuilder("cnpjImportJobConfig", jobRepository)
				.start(cnaeStepBuilder.build())
				.next(reasonStepBuilder.build())
				.next(municipalityImportStepBuilder.build())
				.next(legalNatureImportStepBuilder.build())
				.next(estabilishmentStepBuilder.build())
				.next(companyStepBuilder.build())
				.incrementer(new RunIdIncrementer())
				.build();
		return job;
	}

}
