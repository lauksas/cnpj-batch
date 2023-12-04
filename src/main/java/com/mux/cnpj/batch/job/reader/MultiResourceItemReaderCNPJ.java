package com.mux.cnpj.batch.job.reader;

import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import lombok.extern.slf4j.Slf4j;

@Lazy
@Slf4j
public class MultiResourceItemReaderCNPJ<T> extends MultiResourceItemReader<T> {

	public MultiResourceItemReaderCNPJ(
			Class<T> clazz,
			String FILE_NAME_PATTERN,
			String[] COLUMN_NAMES,
			int[] INCLUDED_FIELDS) {
		Resource[] resources = getResources(FILE_NAME_PATTERN);
		log.info("Found {} resources {} to process", FILE_NAME_PATTERN, resources.length);
		this.setName("multiResourceItemReader");
		this.setResources(resources);
		this.setStrict(true);
		FlatFileReaderCNPJ<T> delegate = new FlatFileReaderCNPJ<T>(clazz, COLUMN_NAMES, INCLUDED_FIELDS);
		this.setDelegate(delegate);
		
	}

	public Resource[] getResources(String resourcePattern) {
		Resource[] resources = new Resource[0];
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		try {
			String locationPattern = "file://" + resourcePattern;
			log.info("locationPattern: {}", locationPattern);
			resources = resolver
					.getResources(locationPattern);

			for (int i = 0; i < resources.length; i++) {
				Resource resource = resources[i];
				log.info("Resource {} path: {}",
						i,
						Paths.get(resource.getURI()).toFile().getAbsolutePath());
			}

		} catch (IOException e) {
			log.error("error loading resources", e);
		}
		return resources;
	}

}
