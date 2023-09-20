package com.mux.cnpj.batch.job.step.factory;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import com.mux.cnpj.batch.job.reader.MultiResourceItemReaderCNPJ;
import com.mux.cnpj.config.ApplicationConfig;

import jakarta.persistence.EntityNotFoundException;

@Component
public class CNPJStepFactory<From, To> {

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private ApplicationConfig config;

	@Autowired
	private JobRepository jobRepository;

	public Step importStep(CNPJStepBuilder<From, To> stepConfig) {

		ItemProcessor<From, To> processor = stepConfig.getProcessor();
		Class<From> csvClass = stepConfig.getCsvClass();
		String filePattern = stepConfig.getFilePattern();
		String[] columnNames = stepConfig.getColumnNames();
		int[] includeColumns = stepConfig.getIncludeColumns();

		String stepBeanName = csvClass.getName() + "Step";
		filePattern = config.getCsvLocation() + "/" + filePattern;

		ItemWriter<To> writer = stepConfig.getWriter();
		
		Step step = new StepBuilder(stepBeanName, jobRepository)
				.<From, To>chunk(config.getChunkSize(), transactionManager)
				.reader(new MultiResourceItemReaderCNPJ<From>(csvClass,
						filePattern,
						columnNames,
						includeColumns))
				.processor(processor)
				.writer(writer)
				.faultTolerant()
				.skip(EntityNotFoundException.class)
				.skipLimit(Integer.MAX_VALUE)
				.build();
		return step;
	}
}
