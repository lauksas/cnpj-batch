package com.mux.cnpj.batch.job;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.mux.cnpj.batch.job.step.CnaeImportStepBuilder;
import com.mux.cnpj.batch.job.step.CompaniesImportStepBuilder;
import com.mux.cnpj.batch.job.step.CountryDataFixTasklet;
import com.mux.cnpj.batch.job.step.CountryImportStepBuilder;
import com.mux.cnpj.batch.job.step.EstabilishmentsImportStepBuilder;
import com.mux.cnpj.batch.job.step.LegalNatureImportStepBuilder;
import com.mux.cnpj.batch.job.step.MunicipalityImportStepBuilder;
import com.mux.cnpj.batch.job.step.PartnersImportStepBuilder;
import com.mux.cnpj.batch.job.step.PartnersQualificationsImportStepBuilder;
import com.mux.cnpj.batch.job.step.PersonTypePopulator;
import com.mux.cnpj.batch.job.step.ReasonDataFixTasklet;
import com.mux.cnpj.batch.job.step.ReasonsImportStepBuilder;
import com.mux.cnpj.batch.job.step.SimpleOptantImportStepBuilder;

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

	@Autowired
	ReasonDataFixTasklet reasonDataFixTasklet;

	@Autowired
	CountryDataFixTasklet countryDataFixTasklet;

	@Autowired
	PersonTypePopulator personTypePopulator;

	@Bean
	public Job importCnpjJob(
			JobRepository jobRepository,
			PlatformTransactionManager platformTransactionManager,
			ReasonsImportStepBuilder reasonStepBuilder,
			MunicipalityImportStepBuilder municipalityImportStepBuilder,
			LegalNatureImportStepBuilder legalNatureImportStepBuilder,
			CnaeImportStepBuilder cnaeStepBuilder,
			PartnersQualificationsImportStepBuilder partnersQualificationsImportStepBuilder,
			CountryImportStepBuilder countryImportStepBuilder,
			EstabilishmentsImportStepBuilder estabilishmentStepBuilder,
			CompaniesImportStepBuilder companyStepBuilder,
			SimpleOptantImportStepBuilder simpleOptantImportStepBuilder,
			PartnersImportStepBuilder partnersImportStepBuilder) {

		Step reasonDataFixStep = new StepBuilder(reasonDataFixTasklet.getClass().getName(), jobRepository)
				.tasklet(reasonDataFixTasklet, platformTransactionManager).build();

		Step countryDataFixStep = new StepBuilder(countryDataFixTasklet.getClass().getName(), jobRepository)
				.tasklet(countryDataFixTasklet, platformTransactionManager).build();

		Step personTypePopulatorFixStep = new StepBuilder(personTypePopulator.getClass().getName(), jobRepository)
				.tasklet(personTypePopulator, platformTransactionManager).build();

		Job job = new JobBuilder("cnpjImportJobConfig", jobRepository)
				.start(cnaeStepBuilder.build())
				.next(reasonStepBuilder.build())
				.next(municipalityImportStepBuilder.build())
				.next(legalNatureImportStepBuilder.build())
				.next(partnersQualificationsImportStepBuilder.build())
				.next(countryImportStepBuilder.build())
				.next(personTypePopulatorFixStep)
				.next(reasonDataFixStep)
				.next(countryDataFixStep)
				.next(estabilishmentStepBuilder.build())
				.next(companyStepBuilder.build())
				.next(simpleOptantImportStepBuilder.build())
				.next(partnersImportStepBuilder.build())
				.incrementer(new RunIdIncrementer())
				.build();
		return job;
	}

}
