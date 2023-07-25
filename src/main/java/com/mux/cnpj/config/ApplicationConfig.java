package com.mux.cnpj.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "app.config")
@Data
public class ApplicationConfig {
	Integer chunkSize;
	String csvLocation;
}
