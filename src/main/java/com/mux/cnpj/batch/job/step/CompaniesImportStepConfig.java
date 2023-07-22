package com.mux.cnpj.batch.job.step;

import static com.mux.cnpj.batch.formatter.CsvFormatter.toBigDecimal;
import static com.mux.cnpj.batch.formatter.CsvFormatter.toInteger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;

import com.mux.cnpj.batch.data.entity.Company;
import com.mux.cnpj.batch.dto.CompanyCsv;
import com.mux.cnpj.batch.job.util.JobUtils;
import com.mux.cnpj.batch.zip.ZIPBufferedReaderFactory;
import com.mux.cnpj.config.DataSourceConfig;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class CompaniesImportStepConfig {
	private static final String BEANS_NAME_PREFIX = "companiesImport";
	public static final String STEP_BEAN_NAME = BEANS_NAME_PREFIX + "Step";
	private static final String RESOURCE_BEAN_NAME = BEANS_NAME_PREFIX + "CsvResource";
	private static final String RESOURCE_READER_BEAN_NAME = BEANS_NAME_PREFIX + "ResourceReader";
	private static final String PROCESSOR_BEAN_NAME = BEANS_NAME_PREFIX + "Processor";
	private static final String WRITER_BEAN_NAME = BEANS_NAME_PREFIX + "Writer";
	private static final String READER_BEAN_NAME = BEANS_NAME_PREFIX + "Reader";
	private static final String LINE_MAPPER_BEAN_NAME = BEANS_NAME_PREFIX + "LineMapper";
	private static final String RESOURCE_LOCATION_PATTERN = "file:/Users/flauksas/Documents/personal/mux-cnpj-dados/import/Empresas*.zip";

	private Resource[] resources = JobUtils.getResources(BEANS_NAME_PREFIX);

	private static final int[] INCLUDED_FIELDS = new int[] { 0, 1, 2, 3, 4, 5, 6 };
	private static final String[] COLUMN_NAMES = new String[] {
			"cnpj_col1_A",
			"nome_col2_B",
			"codNaturezaJuridica_col3_C",
			"motivoSituacaoCadastral_col4_D",
			"socialCapital_col5_E",
			"codPorteEmpresa_col6_F",
			"estadoEUnidadeFederativa_col7_G"
	};

	@Autowired
	@Qualifier(DataSourceConfig.CNPJ_DATASOURCE_BEAN_NAME)
	DataSource cnpjDataSource;

	@Autowired
	ZIPBufferedReaderFactory zipBufferedReaderFactory;

	@Autowired
	PlatformTransactionManager transactionManager;

	@Bean(name = PROCESSOR_BEAN_NAME)
	ItemProcessor<CompanyCsv, Company> processor() {
		return new ItemProcessor<CompanyCsv, Company>() {
			@Override
			@Nullable
			public Company process(@NonNull CompanyCsv csv) throws Exception {

				Company company = Company.builder()
						.cnpj(toInteger(csv.getCnpj_col1_A()))
						.name(csv.getNome_col2_B().trim())
						.legalNature(toInteger(csv.getCodNaturezaJuridica_col3_C()))
						.closeDownReason(toInteger(csv.getMotivoSituacaoCadastral_col4_D()))
						.socialCapital(toBigDecimal(csv.getSocialCapital_col5_E()))
						.companySize(toInteger(csv.getCodPorteEmpresa_col6_F()))
						.build();

				return company;
			}
		};
	}

	@Bean(name = RESOURCE_BEAN_NAME)
	public Resource[] getResources() {
		Resource[] resources = new Resource[0];
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		try {
			resources = resolver
					.getResources(RESOURCE_LOCATION_PATTERN);
		} catch (IOException e) {
			log.error("error loading resources", e);
		}
		return resources;
	}

	@Bean(name = STEP_BEAN_NAME)
	public Step importStep(
			JobRepository jobReposiCompanyry) {
		Step step = new StepBuilder(STEP_BEAN_NAME, jobReposiCompanyry)
				.<CompanyCsv, Company>chunk(100, transactionManager)
				.reader(multiResourceItemReader(resources))
				.processor(processor())
				.writer(jdbcWriter())
				.build();
		return step;
	}

	@Autowired
	EntityManagerFactory entityManagerFactory;

	@Bean(name = WRITER_BEAN_NAME)
	public JpaItemWriter<Company> jpaWritter() {
		return new JpaItemWriterBuilder<Company>().entityManagerFactory(entityManagerFactory).build();
	}

	private static final String query = """
					INSERT INTO cnpj.company
						(cnpj, closedownreason, companysize, legalnature, name, socialcapital)
						select :cnpj, :closeDownReason, :companySize, :legalNature, :name, :socialCapital
						where exists
							(select 1 from cnpj.estabilishment es where es.cnpj=:cnpj limit 1)
					on conflict (cnpj)
						do update set
						cnpj = :cnpj,
						closedownreason = :closeDownReason,
						companysize = :companySize,
						legalnature = :legalNature,
						name = :name,
						socialcapital = :socialCapital
					;
			""";

	@Bean
	JdbcBatchItemWriter<Company> jdbcWriter() {
		JdbcBatchItemWriter<Company> writer = new JdbcBatchItemWriter<>();
		writer.setDataSource(cnpjDataSource);
		writer.setAssertUpdates(false);
		writer.setSql(query);
		writer.setItemSqlParameterSourceProvider(new ItemSqlParameterSourceProvider<Company>() {

			@Override
			public SqlParameterSource createSqlParameterSource(Company item) {
				Map<String, Object> map = new HashMap<>();

				map.put("cnpj", item.getCnpj());
				map.put("closeDownReason", item.getCloseDownReason());
				map.put("companySize", item.getCompanySize());
				map.put("legalNature", item.getLegalNature());
				map.put("name", item.getName());
				map.put("socialCapital", item.getSocialCapital());

				return new MapSqlParameterSource(map);
			}

		});

		return writer;
	}

	@Bean(name = RESOURCE_READER_BEAN_NAME)
	@StepScope
	public MultiResourceItemReader<CompanyCsv> multiResourceItemReader(
			@Qualifier(RESOURCE_BEAN_NAME) Resource[] resources) {
		MultiResourceItemReader<CompanyCsv> reader = new MultiResourceItemReader<>();

		log.info("Found {} resources Company process", resources.length);
		reader.setName("multiResourceItemReader");
		reader.setResources(resources);
		reader.setDelegate(reader());
		return reader;
	}

	@Bean(name = LINE_MAPPER_BEAN_NAME)
	public LineMapper<CompanyCsv> lineMapper() {
		DefaultLineMapper<CompanyCsv> lineMapper = new DefaultLineMapper<CompanyCsv>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(";");
		lineTokenizer.setNames(COLUMN_NAMES);
		lineTokenizer.setIncludedFields(INCLUDED_FIELDS);
		BeanWrapperFieldSetMapper<CompanyCsv> fieldSetMapper = new BeanWrapperFieldSetMapper<CompanyCsv>();
		fieldSetMapper.setTargetType(CompanyCsv.class);
		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		return lineMapper;
	}

	@Bean(name = READER_BEAN_NAME)
	public FlatFileItemReader<CompanyCsv> reader() {
		FlatFileItemReader<CompanyCsv> itemReader = new FlatFileItemReader<CompanyCsv>();
		itemReader.setLineMapper(lineMapper());
		itemReader.setLinesToSkip(0);
		itemReader.setEncoding(StandardCharsets.ISO_8859_1.toString());
		itemReader.setBufferedReaderFactory(zipBufferedReaderFactory);

		return itemReader;
	}

	// private static final String EXISTS_QUERY = "select 1 from cnpj.estabilishment es where es.cnpj = ?";

	// private Integer cnpjExists(String cnpj) {
	// 	Integer exists = null;
	// 	try {
	// 		exists = jdbcTemplate.queryForObject(EXISTS_QUERY, Integer.class, Integer.valueOf(cnpj));
	// 	} catch (EmptyResultDataAccessException noResult) {
	// 		//do nothing.
	// 	}catch (IncorrectResultSizeDataAccessException moreThanOneResult){
	// 		//do nothing.
	// 	}

	// 	return exists;
	// }

}
