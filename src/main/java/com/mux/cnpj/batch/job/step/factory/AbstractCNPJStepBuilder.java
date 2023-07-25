package com.mux.cnpj.batch.job.step.factory;

import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManagerFactory;

public abstract class AbstractCNPJStepBuilder<From, To> implements CNPJStepBuilder<From, To> {
	@Autowired
	private CNPJStepFactory<From, To> factory;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	public Step build() {
		return getFactory().importStep(this);
	}

	public CNPJStepFactory<From, To> getFactory() {
		return factory;
	}

	@Override
	public ItemWriter<To> getWriter() {
		return new JpaItemWriterBuilder<To>().entityManagerFactory(entityManagerFactory)
				.build();
	}

}
