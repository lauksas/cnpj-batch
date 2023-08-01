package com.mux.cnpj.batch.job.step;

import java.util.stream.IntStream;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mux.cnpj.batch.data.entity.Reason;
import com.mux.cnpj.batch.data.repository.ReasonRepository;

@Component
@Transactional
public class ReasonDataFixTasklet implements Tasklet {

	@Autowired
	private ReasonRepository reasonRepository;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Long reasonsCount = reasonRepository.count();

		IntStream.range(0, reasonsCount.intValue()).forEach(id -> {
			boolean idExists = reasonRepository.existsById(id);

			if (!idExists) {
				reasonRepository.saveAndFlush(Reason.builder().id(id).description("INDETERMINADO").build());
			}
		});

		return RepeatStatus.FINISHED;
	}

}
