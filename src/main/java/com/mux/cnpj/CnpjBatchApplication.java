package com.mux.cnpj;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class CnpjBatchApplication {

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job importCnpjJob;

	public static void main(String[] args) {
		SpringApplication.run(CnpjBatchApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void voidStartJob() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		HashMap<String, JobParameter<?>> parameters = new HashMap<>();
		LocalDateTime now = LocalDateTime.now();
		if (now != null) {
			parameters.put("startTime", new JobParameter<LocalDateTime>(now, LocalDateTime.class));
		}

		JobParameters jobParameters = new JobParameters(parameters);
		jobLauncher.run(importCnpjJob, jobParameters);
	}

}
