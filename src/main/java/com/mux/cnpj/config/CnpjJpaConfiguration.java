package com.mux.cnpj.config;

import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mux.cnpj.batch.data.entity.Company;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.mux.cnpj.batch.data", entityManagerFactoryRef = "cnpjEntityManagerFactory", transactionManagerRef = "cnpjTransactionManager")
public class CnpjJpaConfiguration {
	@Bean
	public LocalContainerEntityManagerFactoryBean cnpjEntityManagerFactory(
			@Qualifier("cnpjDataSource") DataSource dataSource,
			EntityManagerFactoryBuilder builder) {
		return builder
				.dataSource(dataSource)
				.packages(Company.class)
				.build();
	}

	@Bean
	@Primary
	public PlatformTransactionManager cnpjTransactionManager(
			@Qualifier("cnpjEntityManagerFactory") LocalContainerEntityManagerFactoryBean cnpjEntityManagerFactory) {
		return new JpaTransactionManager(Objects.requireNonNull(cnpjEntityManagerFactory.getObject()));
	}
}
