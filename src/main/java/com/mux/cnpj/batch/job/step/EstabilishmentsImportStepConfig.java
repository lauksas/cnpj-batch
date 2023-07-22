package com.mux.cnpj.batch.job.step;

import static com.mux.cnpj.batch.formatter.CsvFormatter.intAsText;
import static com.mux.cnpj.batch.formatter.CsvFormatter.nullIfEmpty;
import static com.mux.cnpj.batch.formatter.CsvFormatter.telToInt;
import static com.mux.cnpj.batch.formatter.CsvFormatter.toInteger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
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
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;

import com.mux.cnpj.batch.data.entity.CnpjId;
import com.mux.cnpj.batch.data.entity.Estabilishment;
import com.mux.cnpj.batch.dto.EstablishmentCsv;
import com.mux.cnpj.batch.job.util.JobUtils;
import com.mux.cnpj.batch.zip.ZIPBufferedReaderFactory;
import com.mux.cnpj.config.DataSourceConfig;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class EstabilishmentsImportStepConfig {
	private static final String BEANS_NAME_PREFIX = "estabilishmentImport";
	private static final String RESOURCE_LOCATION_PATTERN = "file:/Users/flauksas/Documents/personal/mux-cnpj-dados/import/Estabelecimentos*.zip";
	public static final String STEP_BEAN_NAME = BEANS_NAME_PREFIX + "Step";
	private static final String RESOURCE_BEAN_NAME = BEANS_NAME_PREFIX + "CsvResource";
	private static final String RESOURCE_READER_BEAN_NAME = BEANS_NAME_PREFIX + "ResourceReader";
	private static final String PROCESSOR_BEAN_NAME = BEANS_NAME_PREFIX + "Processor";
	private static final String WRITER_BEAN_NAME = BEANS_NAME_PREFIX + "Writer";
	private static final String READER_BEAN_NAME = BEANS_NAME_PREFIX + "Reader";
	private static final String LINE_MAPPER_BEAN_NAME = BEANS_NAME_PREFIX + "LineMapper";

	private static final String RJ_CITY_CODE = "6001";
	private static final String ACTIVE_CODE = "02";

	private Resource[] resources = JobUtils.getResources(BEANS_NAME_PREFIX);

	private static final int[] INCLUDED_FIELDS = new int[] { 0, 1, 2, 3, 4, 5, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
			22, 23, 24, 25, 26, 27 };
	private static final String[] COLUMN_NAMES = new String[] {
			"cnpj_colA_1",
			"filial_colB_2",
			"digitoVerificador_colC_3",
			"indMatrizFilialRepetido_colD_4",
			"nomeFantasia_colE_5",
			"situacaoCadastral_colF_6",
			"cnaeFiscalPrincipal_colL_12",
			"cnaeFiscal_colM_13",
			"tipoLograodouro_colN_14",
			"logradouro_colO_15",
			"numero_colP_16",
			"complemento_colQ_17",
			"bairro_colR_18",
			"cep_colS_19",
			"uf_colT_20",
			"codMunicipio_colU_21",
			"ddd1_colV_22",
			"tel1_colW_23",
			"ddd2_colV_24",
			"tel3_colW_25",
			"fax_colV_26",
			"fax_colW_27",
			"correioEletronico_colAB_28",
	};

	@Autowired
	@Qualifier(DataSourceConfig.CNPJ_DATASOURCE_BEAN_NAME)
	DataSource cnpjDataSource;

	@Autowired
	ZIPBufferedReaderFactory zipBufferedReaderFactory;

	@Autowired
	PlatformTransactionManager transactionManager;

	@Bean(name = PROCESSOR_BEAN_NAME)
	ItemProcessor<EstablishmentCsv, Estabilishment> processor() {
		return new ItemProcessor<EstablishmentCsv, Estabilishment>() {
			@Override
			@Nullable
			public Estabilishment process(@NonNull EstablishmentCsv csv) throws Exception {
				String statusId = csv.getSituacaoCadastral_colF_6().trim();

				if (!ACTIVE_CODE.equals(statusId))
					return null;

				String cityCode = csv.getCodMunicipio_colU_21().trim();

				if (!RJ_CITY_CODE.equals(cityCode))
					return null;

				Estabilishment estabilishment = Estabilishment.builder()
						.cnpjId(
								CnpjId.builder().cnpj(
										toInteger(csv.getCnpj_colA_1()))
										.headquartersPart(toInteger(csv.getFilial_colB_2()))
										.checkDigit(toInteger(csv.getDigitoVerificador_colC_3()))
										.build())
						.headquartersIndicator(toInteger(csv.getIndMatrizFilialRepetido_colD_4()))
						.tradeName(nullIfEmpty(csv.getNomeFantasia_colE_5()))
						.statusId(toInteger(statusId))
						.mainCnaeFiscal(toInteger(csv.getCnaeFiscalPrincipal_colL_12()))
						.fiscalCenae(nullIfEmpty(csv.getCnaeFiscal_colM_13()))
						.streetType(nullIfEmpty(csv.getTipoLograodouro_colN_14()))
						.street(nullIfEmpty(csv.getLogradouro_colO_15()))
						.streetNumber(nullIfEmpty(intAsText(csv.getNumero_colP_16())))
						.complement(nullIfEmpty(csv.getComplemento_colQ_17()))
						.district(nullIfEmpty(csv.getBairro_colR_18()))
						.zipCode(telToInt(csv.getCep_colS_19()))
						.stateCode(nullIfEmpty(csv.getUf_colT_20()))
						.cityCode(toInteger(cityCode))
						.areaCode1(toInteger(csv.getDdd1_colV_22()))
						.telephone1(telToInt(csv.getTel1_colW_23()))
						.areaCode2(telToInt(csv.getDdd2_colV_24()))
						.telephone2(telToInt(csv.getDdd2_colV_24()))
						.faxAreaCode(telToInt(csv.getFax_colV_26()))
						.faxTelephone(telToInt(csv.getFax_colW_27()))
						.lastUpdated(LocalDateTime.now())
						.build();

				return estabilishment;
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
	public Step companiesImportStep(
			JobRepository jobReposiCompanyry) {
		Step step = new StepBuilder(STEP_BEAN_NAME, jobReposiCompanyry)
				.<EstablishmentCsv, Estabilishment>chunk(100, transactionManager)
				.reader(multiResourceItemReader(resources))
				.processor(processor())
				.writer(jpaWritter())
				.build();
		return step;
	}

	@Autowired
	EntityManagerFactory entityManagerFactory;

	@Bean(name = WRITER_BEAN_NAME)
	public JpaItemWriter<Estabilishment> jpaWritter() {
		return new JpaItemWriterBuilder<Estabilishment>().entityManagerFactory(entityManagerFactory).build();
	}

	@Bean(name = RESOURCE_READER_BEAN_NAME)
	@StepScope
	public MultiResourceItemReader<EstablishmentCsv> multiResourceItemReader(
			@Qualifier(RESOURCE_BEAN_NAME) Resource[] resources) {
		MultiResourceItemReader<EstablishmentCsv> reader = new MultiResourceItemReader<>();

		log.info("Found {} resources Estabilishment process", resources.length);
		reader.setName("multiResourceItemReader");
		reader.setResources(resources);
		reader.setDelegate(reader());
		return reader;
	}

	@Bean(name = LINE_MAPPER_BEAN_NAME)
	public LineMapper<EstablishmentCsv> lineMapper() {
		DefaultLineMapper<EstablishmentCsv> lineMapper = new DefaultLineMapper<EstablishmentCsv>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(";");
		lineTokenizer.setNames(COLUMN_NAMES);
		lineTokenizer.setIncludedFields(INCLUDED_FIELDS);
		BeanWrapperFieldSetMapper<EstablishmentCsv> fieldSetMapper = new BeanWrapperFieldSetMapper<EstablishmentCsv>();
		fieldSetMapper.setTargetType(EstablishmentCsv.class);
		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		return lineMapper;
	}

	@Bean(name = READER_BEAN_NAME)
	public FlatFileItemReader<EstablishmentCsv> reader() {
		FlatFileItemReader<EstablishmentCsv> itemReader = new FlatFileItemReader<EstablishmentCsv>();
		itemReader.setLineMapper(lineMapper());
		itemReader.setLinesToSkip(0);
		itemReader.setEncoding(StandardCharsets.ISO_8859_1.toString());
		itemReader.setBufferedReaderFactory(zipBufferedReaderFactory);

		return itemReader;
	}

}
