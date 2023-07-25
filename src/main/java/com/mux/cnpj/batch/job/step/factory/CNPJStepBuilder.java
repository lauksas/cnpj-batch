package com.mux.cnpj.batch.job.step.factory;

import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;

public interface CNPJStepBuilder<From, To> {
	ItemProcessor<From, To> getProcessor();

	Class<From> getCsvClass();

	String getFilePattern();

	String[] getColumnNames();

	int[] getIncludeColumns();

	Step build();

	CNPJStepFactory<From, To> getFactory();

	ItemWriter<To> getWriter();

}
