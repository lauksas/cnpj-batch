package com.mux.cnpj.batch.job.reader;

import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Lazy;

@Lazy
public class LineMapperCNPJ<T> extends DefaultLineMapper<T> {

	public LineMapperCNPJ(Class<T> clazz, String[] COLUMN_NAMES, int[] INCLUDED_FIELDS) {
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(";");
		lineTokenizer.setNames(COLUMN_NAMES);
		lineTokenizer.setIncludedFields(INCLUDED_FIELDS);
		BeanWrapperFieldSetMapper<T> fieldSetMapper = new BeanWrapperFieldSetMapper<T>();
		fieldSetMapper.setTargetType(clazz);
		this.setLineTokenizer(lineTokenizer);
		this.setFieldSetMapper(fieldSetMapper);
	}
}
