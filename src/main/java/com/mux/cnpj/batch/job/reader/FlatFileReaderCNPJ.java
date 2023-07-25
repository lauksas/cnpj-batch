package com.mux.cnpj.batch.job.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.springframework.batch.item.file.BufferedReaderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;

import com.mux.cnpj.batch.zip.ZipInputStreamFactory;

import lombok.extern.slf4j.Slf4j;

@Lazy
@Slf4j
public class FlatFileReaderCNPJ<T> extends FlatFileItemReader<T> {

	public FlatFileReaderCNPJ(Class<T> clazz, String[] COLUMN_NAMES, int[] INCLUDED_FIELDS) {
		this.setLinesToSkip(0);
		this.setEncoding(StandardCharsets.ISO_8859_1.toString());
		this.setBufferedReaderFactory(new BufferedReaderFactory() {

			@Override
			public BufferedReader create(Resource resource, String encoding)
					throws UnsupportedEncodingException, IOException {
				log.info("reading zip: {}", resource.getFile().getAbsolutePath());
				return new BufferedReader(
						new InputStreamReader(ZipInputStreamFactory.getInstance().create(resource.getInputStream()), encoding));
			}

		});
		this.setLineMapper(new LineMapperCNPJ<T>(clazz, COLUMN_NAMES, INCLUDED_FIELDS));
	}

	@Override
	public void afterPropertiesSet() {

	}

}
