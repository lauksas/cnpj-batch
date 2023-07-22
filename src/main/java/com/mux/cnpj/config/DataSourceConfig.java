package com.mux.cnpj.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceConfig {

	public static final String BATCH_DATASOURCE_BEAN_NAME = "batchDataSource";
	public static final String CNPJ_DATASOURCE_BEAN_NAME = "cnpjDataSource";

	@Bean(BATCH_DATASOURCE_BEAN_NAME)
	public DataSource batchDataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL)
				.addScript("/org/springframework/batch/core/schema-hsqldb.sql")
				.generateUniqueName(true).build();
	}

	@Bean
	@ConfigurationProperties("spring.datasource.cnpj")
	public DataSourceProperties cnpjDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	public DataSource cnpjDataSource() {
		return cnpjDataSourceProperties()
				.initializeDataSourceBuilder()
				.build();
	}

	@Bean
	public JdbcTemplate cnpjJdbcTemplate(@Qualifier(CNPJ_DATASOURCE_BEAN_NAME) DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	@Primary
	@ConfigurationProperties("spring.datasource.cnpj.hikari")
	public DataSource todosDataSource() {
		return cnpjDataSourceProperties()
				.initializeDataSourceBuilder()
				.build();
	}
}
