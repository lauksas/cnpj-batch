package com.mux.cnpj.batch.job.step;

import java.util.stream.IntStream;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mux.cnpj.batch.data.entity.Country;
import com.mux.cnpj.batch.data.repository.CountryRepository;

@Component
@Transactional
public class CountryDataFixTasklet implements Tasklet {

	@Autowired
	private CountryRepository countryRepository;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Long reasonsCount = countryRepository.getMaxId();

		IntStream.range(0, reasonsCount.intValue()).forEach(id -> {
			boolean idExists = countryRepository.existsById(id);

			if (!idExists) {
				countryRepository.saveAndFlush(
						Country.builder()
								.id(id)
								.name("INDETERMINADO")
								.build());
			}
		});

		return RepeatStatus.FINISHED;
	}

}
