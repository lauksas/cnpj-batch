package com.mux.cnpj.batch.job.writer;

import org.springframework.batch.item.database.JdbcBatchItemWriter;

public class JdbcBatchItemWriterCNPJ<T> extends JdbcBatchItemWriter<T> {

	public void setUsingNamedParameters(boolean usingNamedParameters) {
		this.usingNamedParameters = usingNamedParameters;
	}

}
