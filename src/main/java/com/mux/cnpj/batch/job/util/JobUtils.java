package com.mux.cnpj.batch.job.util;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobUtils {
	public static Resource[] getResources(String resourcePattern) {
		Resource[] resources = new Resource[0];
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		try {
			resources = resolver
					.getResources(resourcePattern);
		} catch (IOException e) {
			log.error("error loading resources", e);
		}
		return resources;
	}
}
