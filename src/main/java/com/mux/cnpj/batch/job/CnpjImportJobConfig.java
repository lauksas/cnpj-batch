package com.mux.cnpj.batch.job;

import javax.sql.DataSource;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.mux.cnpj.batch.job.step.CnaeImportStepBuilder;
import com.mux.cnpj.batch.job.step.CnaePopulator;
import com.mux.cnpj.batch.job.step.CompaniesImportStepBuilder;
import com.mux.cnpj.batch.job.step.CountryDataFixTasklet;
import com.mux.cnpj.batch.job.step.CountryImportStepBuilder;
import com.mux.cnpj.batch.job.step.DownloadFilesTasklet;
import com.mux.cnpj.batch.job.step.EstablishmentsImportStepBuilder;
import com.mux.cnpj.batch.job.step.FullTextSearchIndexRefreshTasklet;
import com.mux.cnpj.batch.job.step.LegalNatureImportStepBuilder;
import com.mux.cnpj.batch.job.step.MunicipalityImportStepBuilder;
import com.mux.cnpj.batch.job.step.PartnersImportStepBuilder;
import com.mux.cnpj.batch.job.step.PartnersQualificationsImportStepBuilder;
import com.mux.cnpj.batch.job.step.PersonTypePopulator;
import com.mux.cnpj.batch.job.step.ReasonDataFixTasklet;
import com.mux.cnpj.batch.job.step.ReasonsImportStepBuilder;
import com.mux.cnpj.batch.job.step.SimpleOptantImportStepBuilder;

import lombok.extern.log4j.Log4j2;

@Configuration
@EnableBatchProcessing(dataSourceRef = "batchDataSource", transactionManagerRef = "batchTransactionManager")
@Log4j2
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

	@Autowired
	DownloadFilesTasklet downloadFilesTasklet;

	@Autowired
	CnaePopulator cnaePopulator;

	@Autowired
	FullTextSearchIndexRefreshTasklet fullTextSearchIndexRefreshTasklet;

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
			EstablishmentsImportStepBuilder establishmentStepBuilder,
			CompaniesImportStepBuilder companyStepBuilder,
			SimpleOptantImportStepBuilder simpleOptantImportStepBuilder,
			PartnersImportStepBuilder partnersImportStepBuilder) {

		Step downloadFilesStep = new StepBuilder(downloadFilesTasklet.getClass().getName(), jobRepository)
				.tasklet(downloadFilesTasklet, platformTransactionManager).build();

		Step reasonDataFixStep = new StepBuilder(reasonDataFixTasklet.getClass().getName(), jobRepository)
				.tasklet(reasonDataFixTasklet, platformTransactionManager).build();

		Step countryDataFixStep = new StepBuilder(countryDataFixTasklet.getClass().getName(), jobRepository)
				.tasklet(countryDataFixTasklet, platformTransactionManager).build();

		Step personTypePopulatorFixStep = new StepBuilder(personTypePopulator.getClass().getName(), jobRepository)
				.tasklet(personTypePopulator, platformTransactionManager).build();

		Step cnaePopulatorFixStep = new StepBuilder(cnaePopulator.getClass().getName(), jobRepository)
				.tasklet(cnaePopulator, platformTransactionManager).build();

		Step fullTextSearchIndexRefreshStep = new StepBuilder(fullTextSearchIndexRefreshTasklet.getClass().getName(),
				jobRepository)
				.tasklet(fullTextSearchIndexRefreshTasklet, platformTransactionManager).build();

		Flow processFilesFLow = new FlowBuilder<Flow>("processFilesFlow")
				.from(cnaeStepBuilder.build())
				.next(reasonStepBuilder.build())
				.next(municipalityImportStepBuilder.build())
				.next(legalNatureImportStepBuilder.build())
				.next(partnersQualificationsImportStepBuilder.build())
				.next(countryImportStepBuilder.build())
				.next(personTypePopulatorFixStep)
				.next(reasonDataFixStep)
				.next(countryDataFixStep)
				.next(cnaePopulatorFixStep)
				.next(establishmentStepBuilder.build())
				.next(companyStepBuilder.build())
				.next(simpleOptantImportStepBuilder.build())
				.next(partnersImportStepBuilder.build())
				.next(fullTextSearchIndexRefreshStep)
				.build();

		Job job = new JobBuilder("cnpjImportJobConfig", jobRepository)
				.start(downloadFilesStep)
				.on("*")
				.to(new JobExecutionDecider() {
					@Override
					public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
						if (stepExecution.getExitStatus().equals(ExitStatus.STOPPED)) {
							return FlowExecutionStatus.COMPLETED;
						} else
							return new FlowExecutionStatus("PROCESS_FILES");
					}
				})
				.on(FlowExecutionStatus.COMPLETED.toString()).stop()
				.on("PROCESS_FILES")
				.to(processFilesFLow)
				.end()
				.listener(new SkipListener<Object, Object>() {
					@Override
					public void onSkipInWrite(Object item, Throwable t) {
						SkipListener.super.onSkipInWrite(item, t);
						log.warn("record was skipet", t);
					}
				})
				.build();
		return job;
	}

}
