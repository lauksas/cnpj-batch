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

import com.mux.cnpj.batch.data.entity.Cnae;
import com.mux.cnpj.batch.data.repository.CnaeRepository;

@Component
@Transactional
public class CnaePopulator implements Tasklet {

	@Autowired
	private CnaeRepository cnaeRepository;

	private static final String UNKOWN_DESC = "INDETERMINADO";

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		List<Cnae> cnaes = new ArrayList<>();
		cnaes.add(Cnae.builder().id(4761000).description(UNKOWN_DESC).build());
		cnaes.add(Cnae.builder().id(6202100).description(UNKOWN_DESC).build());

		cnaeRepository.saveAll(cnaes);

		return RepeatStatus.FINISHED;
	}

}
