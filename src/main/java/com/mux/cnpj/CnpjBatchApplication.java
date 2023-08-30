package com.mux.cnpj;

import java.io.IOException;
import java.util.HashMap;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
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

import com.mux.cnpj.batch.client.CnpjClient;

@SpringBootApplication
public class CnpjBatchApplication {

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job importCnpjJob;

	@Autowired
	CnpjClient cnpjClient;

	public static void main(String[] args) {
		SpringApplication.run(CnpjBatchApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void voidStartJob() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException, IOException {
		HashMap<String, JobParameter<?>> parameters = new HashMap<>();
		String filesTimeStemp = cnpjClient.getUpdatedFilesTimeStampCsv();
		if (filesTimeStemp != null) {
			parameters.put("filesTimeStamp", new JobParameter<String>(filesTimeStemp, String.class));
		}

		JobParameters jobParameters = new JobParameters(parameters);
		JobExecution execution = jobLauncher.run(importCnpjJob, jobParameters);
		ExitStatus exitStatus = execution.getExitStatus();
		if (ExitStatus.FAILED.equals(exitStatus)) {
			System.exit(1);
		}
	}

}
