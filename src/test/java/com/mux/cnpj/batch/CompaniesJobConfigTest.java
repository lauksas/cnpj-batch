package com.mux.cnpj.batch;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CompaniesJobConfigTest {

	// @Autowired
	// JobLauncher jobLauncher;

	// @Autowired
	// Job readCSVFileJob;

	@Test
	public void testCompaniesCsvImport() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {

		// HashMap<String, JobParameter<?>> parameters = new HashMap<>();
		// parameters.put("startTime", new JobParameter<LocalDateTime>(LocalDateTime.now(), LocalDateTime.class));
		// JobParameters jobParameters = new JobParameters(parameters);
		// jobLauncher.run(readCSVFileJob, jobParameters);
	}
}
