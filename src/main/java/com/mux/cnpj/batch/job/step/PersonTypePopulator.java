package com.mux.cnpj.batch.job.step;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mux.cnpj.batch.data.entity.PersonType;
import com.mux.cnpj.batch.data.repository.PersonTypeRepository;

@Component
@Transactional
public class PersonTypePopulator implements Tasklet {

	@Autowired
	private PersonTypeRepository personTypeRepository;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		List<PersonType> persons = new ArrayList<>(3);
		persons.add(PersonType.builder().id(1).description("JURIDICA").build());
		persons.add(PersonType.builder().id(2).description("FISICA").build());
		persons.add(PersonType.builder().id(3).description("ESTRANGEIRO").build());

		personTypeRepository.saveAllAndFlush(persons);

		return RepeatStatus.FINISHED;
	}

}
