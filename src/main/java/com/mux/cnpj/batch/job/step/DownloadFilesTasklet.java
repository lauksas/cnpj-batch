package com.mux.cnpj.batch.job.step;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mux.cnpj.batch.client.CnpjClient;
import com.mux.cnpj.batch.client.NoFileUpdatedException;
import com.mux.cnpj.config.ApplicationConfig;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DownloadFilesTasklet implements Tasklet {

	@Autowired
	CnpjClient cnpjClient;

	@Autowired
	ApplicationConfig applicationConfig;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		try {
			cnpjClient.updateFilesOnDisk();
		} catch (NoFileUpdatedException e) {

			if (applicationConfig.getForceReimport()) {
				log.info("no file was modified but forcing re-import.");
			} else {
				log.info("no file was modified, returning FINISHED.");

				contribution.setExitStatus(ExitStatus.STOPPED);
				return RepeatStatus.FINISHED;
			}

		}
		contribution.setExitStatus(ExitStatus.COMPLETED);
		log.info("at least one file was modified, returning CONTINUABLE");
		return RepeatStatus.FINISHED;
	}

}
