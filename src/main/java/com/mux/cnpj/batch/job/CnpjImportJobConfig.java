package com.mux.cnpj.batch.job;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.support.JdbcTransactionManager;

import com.mux.cnpj.batch.job.step.CompaniesImportStepConfig;
import com.mux.cnpj.batch.job.step.EstabilishmentsImportStepConfig;
import com.mux.cnpj.batch.job.step.GenericStepConfig;

@Configuration
@EnableBatchProcessing(dataSourceRef = "batchDataSource", transactionManagerRef = "batchTransactionManager")
@Import({ GenericStepConfig.class })
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
			@Qualifier(CompaniesImportStepConfig.STEP_BEAN_NAME) Step companiesImportStep,
			@Qualifier(EstabilishmentsImportStepConfig.STEP_BEAN_NAME) Step estabilishmentImportStep) {
		Job job = new JobBuilder("cnpjImportJobConfig", jobRepository)
				// .start(estabilishmentImportStep)
				.start(companiesImportStep)
				.incrementer(new RunIdIncrementer())
				.build();
		return job;
	}

}
