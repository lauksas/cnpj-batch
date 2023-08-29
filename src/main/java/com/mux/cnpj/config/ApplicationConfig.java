package com.mux.cnpj.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "app.config")
@Data
public class ApplicationConfig {
	Integer chunkSize;
	String csvLocation;
	String csvCityCodeToImport;

	public List<String> getCityCodesToImport() {
		String[] codes = {};
		if (csvCityCodeToImport != null && !csvCityCodeToImport.isEmpty()) {
			codes = csvCityCodeToImport.split(",");
		}
		return Arrays.asList(codes);
	}
}
